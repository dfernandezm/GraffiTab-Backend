package com.graffitab.server.api.controller.user;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.controller.BaseApiController;
import com.graffitab.server.api.dto.ActionCompletedResult;
import com.graffitab.server.api.dto.user.CreateExternalUserResult;
import com.graffitab.server.api.dto.user.CreateUserResult;
import com.graffitab.server.api.dto.user.ExternalProviderDto;
import com.graffitab.server.api.dto.user.ExternalUserDto;
import com.graffitab.server.api.dto.user.GetUserProfileResult;
import com.graffitab.server.api.dto.user.GetUserResult;
import com.graffitab.server.api.dto.user.ListUsersResult;
import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.api.dto.user.UserProfileDto;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.api.util.UploadUtils;
import com.graffitab.server.persistence.model.PagedList;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.PagingService;
import com.graffitab.server.service.UserService;
import com.graffitab.server.util.GuidGenerator;

@RestController
@RequestMapping("/api/users")
public class UserApiController extends BaseApiController {

	private static Logger LOG = LogManager.getLogger();

	@Resource
	private UserService userService;

	@Resource
	private PagingService<User> pagingService;

	@Resource
	private OrikaMapper mapper;

	@Resource
	private UploadUtils uploadUtils;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public GetUserResult getUser(@PathVariable("id") Long id) {

		GetUserResult getUserResult = new GetUserResult();

		User user = userService.findUserById(id);
		getUserResult.setUser(mapper.map(user, UserDto.class));

		return getUserResult;
	}

	@RequestMapping(value = "/username/{username}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public GetUserResult getUserByUsername(@PathVariable("username") String username) {

		GetUserResult getUserResult = new GetUserResult();

		User user = (User) userService.getUserByUsername(username);
		getUserResult.setUser(mapper.map(user, UserDto.class));

		return getUserResult;
	}

	@RequestMapping(value = "/externalprovider/login", method = RequestMethod.POST)
	@Transactional()
	public GetUserResult verifyExternalId(@JsonProperty("externalProvider") ExternalProviderDto externalProviderDto) {

		GetUserResult getUserResult = new GetUserResult();

		User user = userService.verifyExternalProvider(externalProviderDto.getExternalId(), externalProviderDto.getAccessToken(), externalProviderDto.getExternalProviderType());
		getUserResult.setUser(mapper.map(user, UserDto.class));

		return getUserResult;
	}

	@RequestMapping(value = {"/externalprovider"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public CreateExternalUserResult createExternalUser(@RequestBody ExternalUserDto externalUserDto) {

		CreateExternalUserResult createExternalUserResult = new CreateExternalUserResult();

		User user = userService.createExternalUser(mapper.map(externalUserDto.getUser(), User.class), externalUserDto.getExternalId(), externalUserDto.getAccessToken(), externalUserDto.getExternalProviderType());
		createExternalUserResult.setUser(mapper.map(user, UserDto.class));

		return createExternalUserResult;
	}

	@RequestMapping(value = {""}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public CreateUserResult createUser(@JsonProperty("user") UserDto userDto) {

		CreateUserResult createUserResult = new CreateUserResult();

		String userToken = GuidGenerator.generate();
		User user = userService.createUser(mapper.map(userDto, User.class), userToken);
		createUserResult.setUser(mapper.map(user, UserDto.class));
		createUserResult.setToken(user.getMetadataItems().get(UserService.ACTIVATION_TOKEN_METADATA_KEY));

		return createUserResult;
	}

	@RequestMapping(value = "/activate/{token}", method = RequestMethod.GET)
	@Transactional
	public ActionCompletedResult activateAccount(@PathVariable("token") String token) {

		ActionCompletedResult activateUserResult = new ActionCompletedResult();

		userService.activateUser(token);

		return activateUserResult;
	}

	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	public ActionCompletedResult resetPassword(@JsonProperty(value = "email") String email) {

		ActionCompletedResult resetPasswordResult = new ActionCompletedResult();

		userService.resetPassword(email);

		return resetPasswordResult;
	}

	@RequestMapping(value = "/resetpassword/{token}", method = RequestMethod.PUT)
	public ActionCompletedResult completePasswordReset(@PathVariable(value = "token") String token,
													   @JsonProperty(value = "password") String password) {

		ActionCompletedResult resetPasswordResult = new ActionCompletedResult();

		userService.completePasswordReset(token, password);

		return resetPasswordResult;
	}

	// Maybe not needed - get all the users page by page
	@RequestMapping(value = {""}, method = RequestMethod.GET, produces={"application/json"})
	@Transactional
	public ListUsersResult listUsers(@RequestParam(value="offset", required = false) Integer offset,
									 @RequestParam(value="count", required = false) Integer count) {

		ListUsersResult listUsersResult = new ListUsersResult();

		PagedList<User> users =  pagingService.findAllPaged(offset, count);

		List<UserDto> userDtos = mapper.mapList(users, UserDto.class);
		listUsersResult.setUsers(userDtos);
		listUsersResult.setTotal(users.getTotal());
		listUsersResult.setPageSize(users.getCount());
		listUsersResult.setOffset(users.getOffset());

		return listUsersResult;
	}

	@RequestMapping(value = {"/{id}/profile"}, method = RequestMethod.GET)
	@Transactional
	public GetUserProfileResult getUserProfile(@PathVariable("id") Long id) {

		GetUserProfileResult userProfileResult = new GetUserProfileResult();

		User user = userService.getUserProfile(id);
		userProfileResult.setUser(mapper.map(user, UserProfileDto.class));

		return userProfileResult;
	}

	@RequestMapping(value = {"/{id}/follow"}, method = RequestMethod.POST)
	@Transactional
	public GetUserProfileResult follow(@PathVariable("id") Long userId) {

		GetUserProfileResult userProfileResult = new GetUserProfileResult();

		User toFollow = userService.follow(userId);
		userProfileResult.setUser(mapper.map(toFollow, UserProfileDto.class));

		return userProfileResult;
	}

	@RequestMapping(value = {"/{id}/unfollow"}, method = RequestMethod.POST)
	@Transactional
	public GetUserProfileResult unFollow(@PathVariable("id") Long userId) {

		GetUserProfileResult userProfileResult = new GetUserProfileResult();

		User toUnfollow = userService.unfollow(userId);
		userProfileResult.setUser(mapper.map(toUnfollow, UserProfileDto.class));

		return userProfileResult;
	}

	@RequestMapping(value = {"/{id}/followers"}, method = RequestMethod.GET)
	@Transactional
	public ListUsersResult getFollowers(@PathVariable("id") Long userId,
										@RequestParam(value="offset", required = false) Integer offset,
										@RequestParam(value="count", required = false) Integer count) {

		return userService.getFollowingOrFollowersResultForUser(true, userId, offset, count);
	}

	@RequestMapping(value = {"/{id}/following"}, method = RequestMethod.GET)
	@Transactional
	public ListUsersResult getFollowing(@PathVariable("id") Long userId,
										@RequestParam(value="offset", required = false) Integer offset,
										@RequestParam(value="count", required = false) Integer count) {

		return userService.getFollowingOrFollowersResultForUser(false, userId, offset, count);
	}

	//TODO: * fullProfile /api/users/me
	//TODO: Most active users -> /api/users/mostactive page by page
	//TODO: getSocialFriends -> /api/users/socialfriends page by page
}

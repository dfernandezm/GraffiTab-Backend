package com.graffitab.server.api.user;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.BaseApiController;
import com.graffitab.server.api.dto.asset.AddAssetResult;
import com.graffitab.server.api.dto.asset.AssetDto;
import com.graffitab.server.api.dto.user.CreateUserResult;
import com.graffitab.server.api.dto.user.GetUserProfileResult;
import com.graffitab.server.api.dto.user.GetUserResult;
import com.graffitab.server.api.dto.user.ListUsersResult;
import com.graffitab.server.api.dto.user.UpdateUserResult;
import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.api.dto.user.UserProfileDto;
import com.graffitab.server.api.errors.EntityNotFoundException;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.api.errors.ValidationErrorException;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.api.util.UploadUtils;
import com.graffitab.server.persistence.model.Asset;
import com.graffitab.server.persistence.model.AssetType;
import com.graffitab.server.persistence.model.PagedList;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.PagingService;
import com.graffitab.server.service.UserService;

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
	public GetUserResult getUser(@PathVariable("id") Long id, HttpServletRequest request) {

		GetUserResult getUserResult = new GetUserResult();
		User user = userService.findUserById(id);

		if (user != null) {
			LOG.info("Returning user with id " + id);
			getUserResult.setUser(mapper.map(user, UserDto.class));
		} else {
			throw new EntityNotFoundException(ResultCode.USER_NOT_FOUND, "Could not find user with id " + id);
		}

		return getUserResult;
	}

	@RequestMapping(value = "/username/{username}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public GetUserResult getUserByUsername(@PathVariable("username") String username) {

		GetUserResult getUserResult;
		User user;
		try {
			getUserResult = new GetUserResult();
			user = (User) userService.findUserByUsername(username);

			getUserResult.setUser(mapper.map(user, UserDto.class));
		} catch (UsernameNotFoundException e) {
			LOG.info("Could not find user " + username);
			throw new EntityNotFoundException(ResultCode.USER_NOT_FOUND, "Could not find user " + username);
		}

		return getUserResult;
	}

	@RequestMapping(value = {"","/register"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public CreateUserResult createUser(@JsonProperty("user") UserDto userDto) {

		CreateUserResult createUserResult = new CreateUserResult();

		if (validateUser(userDto)){

			if (userDto.getId() == null) {

				User user = mapper.map(userDto, User.class);
				userService.saveUser(user);

				UserDto outputUser = mapper.map(user, UserDto.class);
				outputUser.setPassword(null);
				createUserResult.setUser(outputUser);

			} else {

				throw new RestApiException(ResultCode.BAD_REQUEST, "ID has been provided to create endpoint -- This is not allowed");
			}

		} else {

			throw new ValidationErrorException("Validation error creating user");
		}

		return createUserResult;
	}

	@RequestMapping(value = {"/{id}"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public UpdateUserResult updateUser(@PathVariable("id") Long id, @JsonProperty("user") UserDto userDto) {

		UpdateUserResult updateUserResult = new UpdateUserResult();

		if (validateUser(userDto)) {

			User user = userService.findUserById(id);
			mapper.map(userDto, user);

			LOG.info("Updated user with ID " + user.getId());
			updateUserResult.setUser(mapper.map(user, UserDto.class));
			return updateUserResult;

		} else {
			throw new ValidationErrorException("Validation error updating user");
		}
	}

	@RequestMapping(value = {""}, method = RequestMethod.GET, produces={"application/json"})
	@Transactional
	public ListUsersResult listUsers(@RequestParam(value="offset", required = false) Integer offset,
									 @RequestParam(value="count", required = false) Integer count,
									 @AuthenticationPrincipal String user) {

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
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public GetUserProfileResult getUserProfile(@PathVariable("id") Long id) {

		GetUserProfileResult userProfileResult = new GetUserProfileResult();

		//TODO: Find user profile
		User user = userService.findUserById(id);

		if (user != null) {
			LOG.info("Returning user profile " + id);
			userProfileResult.setUser(mapper.map(user, UserProfileDto.class));
		} else {
			throw new EntityNotFoundException(ResultCode.NOT_FOUND, "Could not find user with id " + id);
		}

		return userProfileResult;
	}

	@RequestMapping(value = {"/avatar"}, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public AddAssetResult addAvatarForUser(HttpServletRequest request) throws IOException {
		return addAssetToCurrentUser(request, AssetType.AVATAR);
	}

	@RequestMapping(value = {"/cover"}, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public AddAssetResult addCoverForUser(HttpServletRequest request) {
		return addAssetToCurrentUser(request, AssetType.COVER);
	}

	private AddAssetResult addAssetToCurrentUser(HttpServletRequest request, AssetType assetType) {
		AddAssetResult result = new AddAssetResult();
		try {
			long contentLength = request.getContentLengthLong();
			Asset asset = userService.addAssetToCurrentUser(request.getInputStream(), assetType, contentLength);
			AssetDto assetDto = mapper.map(asset, AssetDto.class);
			result.setAsset(assetDto);
		} catch (IOException e) {
			String msg = "Error reading InputStream";
			LOG.error(msg, e);
			throw new RestApiException(msg);
		}

		return result;
	}

	@RequestMapping(value = {"/{id}/follow"}, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public GetUserProfileResult follow(@PathVariable("id") Long userId) {

		GetUserProfileResult userProfileResult = new GetUserProfileResult();
		User toFollow = userService.findUserById(userId);

		if (toFollow != null) {
			User currentUser = userService.getCurrentUser();
			currentUser.follow(toFollow);
			userProfileResult.setUser(mapper.map(currentUser, UserProfileDto.class));
		} else {
			throw new RestApiException(ResultCode.USER_NOT_FOUND, "User with id " + userId + " not found");
		}

		return userProfileResult;
	}

	@RequestMapping(value = {"/{id}/unfollow"}, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public GetUserProfileResult unFollow(@PathVariable("id") Long userId) {

		GetUserProfileResult userProfileResult = new GetUserProfileResult();
		User toUnfollow = userService.findUserById(userId);

		if (toUnfollow != null) {
			User currentUser = userService.getCurrentUser();
			currentUser.unfollow(toUnfollow);
			userProfileResult.setUser(mapper.map(currentUser, UserProfileDto.class));
		} else {
			throw new RestApiException(ResultCode.USER_NOT_FOUND, "User with id " + userId + " not found");
		}

		return userProfileResult;
	}


	@RequestMapping(value = {"/followers"}, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public ListUsersResult getFollowers() {
		ListUsersResult result = new ListUsersResult();
		//TODO: Use query
		//List<UserProfileDto> followers = mapper.mapList(new ArrayList<>(userService.getCurrentUser().getFollowers()), UserProfileDto.class);
		return result;
	}

	private Boolean validateUser(UserDto userDto) {

		boolean validationResult = false;

		if ( StringUtils.isEmpty(userDto.getUsername()) || StringUtils.isEmpty(userDto.getEmail()) ||
			 StringUtils.isEmpty(userDto.getFirstName()) || StringUtils.isEmpty(userDto.getLastName()) ||
			 StringUtils.isEmpty(userDto.getPassword())) {

			validationResult = false;

		} else {

			if (isUsernameTaken(userDto.getUsername(),userDto.getId()) || isEmailTaken(userDto.getEmail(),userDto.getId()) ){
				validationResult = false;
			} else {
				validationResult = true;
			}
		}

		return validationResult;
	}

	private Boolean isUsernameTaken(String username, Long userId) {
		if ( userId != null){
			return  !userService.findUsersWithUsername(username, userId).isEmpty();
		} else {
			return  !userService.findByUsername(username).isEmpty();
		}
	}

	private Boolean isEmailTaken(String email, Long userId) {
		if ( userId != null){
			return  !userService.findUsersWithEmail(email, userId).isEmpty();
		} else {
			return  !userService.findByEmail(email).isEmpty();
		}
	}
}

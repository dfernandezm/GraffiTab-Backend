package com.graffitab.server.api.controller.user;

import javax.annotation.Resource;

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
import com.graffitab.server.api.dto.ListItemsResult;
import com.graffitab.server.api.dto.streamable.StreamableDto;
import com.graffitab.server.api.dto.user.ExternalUserDto;
import com.graffitab.server.api.dto.user.FullUserDto;
import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.api.dto.user.result.GetFullUserResult;
import com.graffitab.server.api.dto.user.result.GetUserResult;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.persistence.model.user.User.AccountStatus;
import com.graffitab.server.service.streamable.StreamableService;
import com.graffitab.server.service.user.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController extends BaseApiController {

	@Resource
	private UserService userService;

	@Resource
	private StreamableService streamableService;

	@Resource
	private OrikaMapper mapper;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetUserResult getUser(@PathVariable("id") Long id) {
		GetUserResult getUserResult = new GetUserResult();
		User user = userService.findUserById(id);
		getUserResult.setUser(mapper.map(user, UserDto.class));
		return getUserResult;
	}

	@RequestMapping(value = "/username/{username}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetUserResult getUserByUsername(@PathVariable("username") String username) {
		GetUserResult getUserResult = new GetUserResult();
		User user = (User) userService.getUserByUsername(username);
		getUserResult.setUser(mapper.map(user, UserDto.class));
		return getUserResult;
	}

	@RequestMapping(value = {"/externalproviders"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	public ActionCompletedResult createExternalUser(@RequestBody ExternalUserDto externalUserDto) {
		userService.createExternalUser(mapper.map(externalUserDto.getUser(), User.class), externalUserDto.getExternalId(), externalUserDto.getAccessToken(), externalUserDto.getExternalProviderType());
		return new ActionCompletedResult();
	}

	@RequestMapping(value = {""}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public ActionCompletedResult createUser(@JsonProperty("user") UserDto userDto) {
		userService.createUser(mapper.map(userDto, User.class));
		return new ActionCompletedResult();
	}

	@RequestMapping(value = "/activate/{token}", method = RequestMethod.GET)
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
	@Transactional
	public ActionCompletedResult completePasswordReset(
			@PathVariable(value = "token") String token,
			@JsonProperty(value = "password") String password) {
		ActionCompletedResult resetPasswordResult = new ActionCompletedResult();
		userService.completePasswordReset(token, password);
		return resetPasswordResult;
	}

	@RequestMapping(value = {"/{id}/profile"}, method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetFullUserResult getUserProfile(@PathVariable("id") Long id) {
		GetFullUserResult userProfileResult = new GetFullUserResult();
		User user = userService.getUserProfile(id);
		userProfileResult.setUser(mapper.map(user, FullUserDto.class));
		return userProfileResult;
	}

	@RequestMapping(value = {"/{id}/followers"}, method = RequestMethod.POST)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetFullUserResult follow(@PathVariable("id") Long userId) {
		GetFullUserResult userProfileResult = new GetFullUserResult();
		User toFollow = userService.follow(userId);
		userProfileResult.setUser(mapper.map(toFollow, FullUserDto.class));
		return userProfileResult;
	}

	@RequestMapping(value = {"/{id}/followers"}, method = RequestMethod.DELETE)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetFullUserResult unFollow(@PathVariable("id") Long userId) {
		GetFullUserResult userProfileResult = new GetFullUserResult();
		User toUnfollow = userService.unfollow(userId);
		userProfileResult.setUser(mapper.map(toUnfollow, FullUserDto.class));
		return userProfileResult;
	}

	@RequestMapping(value = {"/{id}/followers"}, method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListItemsResult<UserDto> getFollowers(
			@PathVariable("id") Long userId,
			@RequestParam(value="offset", required = false) Integer offset,
			@RequestParam(value="limit", required = false) Integer limit) {
		return userService.getFollowingOrFollowersForUserResult(true, userId, offset, limit);
	}

	@RequestMapping(value = {"/{id}/following"}, method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListItemsResult<UserDto> getFollowing(
			@PathVariable("id") Long userId,
			@RequestParam(value="offset", required = false) Integer offset,
			@RequestParam(value="limit", required = false) Integer limit) {
		return userService.getFollowingOrFollowersForUserResult(false, userId, offset, limit);
	}

	@RequestMapping(value = {"/{id}/streamables"}, method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListItemsResult<StreamableDto> getStreamables(
			@PathVariable("id") Long userId,
			@RequestParam(value="offset", required = false) Integer offset,
			@RequestParam(value="limit", required = false) Integer limit) {
		return streamableService.getUserStreamablesResult(userId, offset, limit);
	}

	@RequestMapping(value = {"/{id}/feed"}, method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListItemsResult<StreamableDto> getFeed(
			@PathVariable("id") Long userId,
			@RequestParam(value="offset", required = false) Integer offset,
			@RequestParam(value="limit", required = false) Integer limit) {
		return streamableService.getUserFeedResult(userId, offset, limit);
	}

	@RequestMapping(value = {"/mostactive"}, method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListItemsResult<UserDto> getMostActiveUsers(
			@RequestParam(value="offset", required = false) Integer offset,
			@RequestParam(value="limit", required = false) Integer limit) {
		return userService.getMostActiveUsersResult(offset, limit);
	}

	@RequestMapping(value = {"/{id}/liked"}, method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListItemsResult<StreamableDto> getLikedStreamablesForUser(
			@PathVariable("id") Long userId,
			@RequestParam(value="offset", required = false) Integer offset,
			@RequestParam(value="limit", required = false) Integer limit) {
		return streamableService.getLikedStreamablesForUserResult(userId, offset, limit);
	}

	@RequestMapping(value = {"/search"}, method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListItemsResult<UserDto> searchUsers(
			@RequestParam(value="query", required = true) String query,
			@RequestParam(value="offset", required = false) Integer offset,
			@RequestParam(value="limit", required = false) Integer limit) {
		return userService.searchUsersResult(query, offset, limit);
	}
}

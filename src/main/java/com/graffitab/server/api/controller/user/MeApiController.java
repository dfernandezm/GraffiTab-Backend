package com.graffitab.server.api.controller.user;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.dto.ActionCompletedResult;
import com.graffitab.server.api.dto.asset.AddAssetResult;
import com.graffitab.server.api.dto.asset.AssetDto;
import com.graffitab.server.api.dto.device.DeviceDto;
import com.graffitab.server.api.dto.user.ChangePasswordDto;
import com.graffitab.server.api.dto.user.ExternalProviderDto;
import com.graffitab.server.api.dto.user.ExternalProviderDto.ExternalProviderType;
import com.graffitab.server.api.dto.user.GetUserResult;
import com.graffitab.server.api.dto.user.ListUsersResult;
import com.graffitab.server.api.dto.user.UpdateUserResult;
import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.model.Asset;
import com.graffitab.server.persistence.model.Asset.AssetType;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.User.AccountStatus;
import com.graffitab.server.service.DeviceService;
import com.graffitab.server.service.UserService;

@RestController
@RequestMapping("/api/users/me")
public class MeApiController {

	private static Logger LOG = LogManager.getLogger();

	@Resource
	private UserService userService;

	@Resource
	private DeviceService deviceService;

	@Resource
	private OrikaMapper mapper;

	@RequestMapping(value = "", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetUserResult getMe() {
		GetUserResult getUserResult = new GetUserResult();
		User user = userService.getCurrentUser();
		getUserResult.setUser(mapper.map(user, UserDto.class));
		return getUserResult;
	}

	@RequestMapping(value = {""}, method = RequestMethod.POST, consumes={"application/json"})
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public UpdateUserResult updateUser(@JsonProperty("user") UserDto userDto) {
		UpdateUserResult updateUserResult = new UpdateUserResult();
		User user = userService.updateUser(mapper.map(userDto, User.class));
		updateUserResult.setUser(mapper.map(user, UserDto.class));
		return updateUserResult;
	}

	@RequestMapping(value = "/device", method = RequestMethod.POST)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ActionCompletedResult registerDevice(@JsonProperty("device") DeviceDto deviceDto) {
		ActionCompletedResult actionCompletedResult = new ActionCompletedResult();
		deviceService.registerDevice(deviceDto.getToken(), deviceDto.getOsType());
		return actionCompletedResult;
	}

	@RequestMapping(value = "/device", method = RequestMethod.DELETE)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ActionCompletedResult unregisterDevice(@JsonProperty("device") DeviceDto deviceDto) {
		ActionCompletedResult actionCompletedResult = new ActionCompletedResult();
		deviceService.unregisterDevice(deviceDto.getToken(), deviceDto.getOsType());
		return actionCompletedResult;
	}

	@RequestMapping(value = "/externalprovider", method = RequestMethod.POST)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ActionCompletedResult linkExternalProvider(@JsonProperty("externalProvider") ExternalProviderDto externalProviderDto) {
		ActionCompletedResult actionCompletedResult = new ActionCompletedResult();
		userService.linkExternalProvider(externalProviderDto.getExternalId(), externalProviderDto.getAccessToken(), externalProviderDto.getExternalProviderType());
		return actionCompletedResult;
	}

	@RequestMapping(value = "/externalprovider", method = RequestMethod.DELETE)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ActionCompletedResult unlinkExternalProvider(@JsonProperty("externalProviderType") ExternalProviderType externalProviderType) {
		ActionCompletedResult actionCompletedResult = new ActionCompletedResult();
		userService.unlinkExternalProvider(externalProviderType);
		return actionCompletedResult;
	}

	@RequestMapping(value = {"/changepassword"}, method = RequestMethod.POST)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ActionCompletedResult changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
		ActionCompletedResult getUserResult = new ActionCompletedResult();
		userService.changePassword(changePasswordDto.getCurrentPassword(), changePasswordDto.getNewPassword());
		return getUserResult;
	}

	@RequestMapping(value = {"/avatar"}, method = RequestMethod.POST)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public AddAssetResult addAvatarForUser(HttpServletRequest request) throws IOException {
		return addAssetToCurrentUser(request, AssetType.AVATAR);
	}

	@RequestMapping(value = {"/cover"}, method = RequestMethod.POST)
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public AddAssetResult addCoverForUser(HttpServletRequest request) {
		return addAssetToCurrentUser(request, AssetType.COVER);
	}

	@RequestMapping(value = {"/followers"}, method = RequestMethod.GET)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListUsersResult getFollowersForCurrentUser(@RequestParam(value="offset", required = false) Integer offset,
													  @RequestParam(value="count", required = false) Integer count) {
		return userService.getFollowingOrFollowersResultForUser(true, null, offset, count);
	}

	@RequestMapping(value = {"/following"}, method = RequestMethod.GET)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public ListUsersResult getFollowingForCurrentUser(@RequestParam(value="offset", required = false) Integer offset,
													  @RequestParam(value="count", required = false) Integer count) {
		return userService.getFollowingOrFollowersResultForUser(false, null, offset, count);
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
}

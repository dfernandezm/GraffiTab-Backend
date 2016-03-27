package com.graffitab.server.service.social;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.graffitab.server.api.dto.ListItemsResult;
import com.graffitab.server.api.dto.user.ExternalProviderDto.ExternalProviderType;
import com.graffitab.server.api.dto.user.UserSocialFriendsContainerDto;
import com.graffitab.server.persistence.model.PagedList;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.persistence.model.user.UserSocialFriendsContainer;
import com.graffitab.server.service.paging.PagingService;
import com.graffitab.server.service.user.UserService;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class SocialNetworksService {

	@Resource
	private UserService userService;

	@Resource
	private PagingService pagingService;

	@Resource
	private FacebookSocialNetworkService facebookService;

	public ListItemsResult<UserSocialFriendsContainerDto> getSocialFriendsResult(Integer offset, Integer limit) {
		User currentUser = userService.getCurrentUser();
		List<UserSocialFriendsContainer> containers = new ArrayList<>();

		for (ExternalProviderType type : ExternalProviderType.values()) {
			UserSocialFriendsContainer container = getFriendsForSocialNetwork(currentUser, type, offset, limit);
			if (container != null) {
				containers.add(container);
			}
		}

		return pagingService.mapResults(new PagedList<>(containers, null, null), UserSocialFriendsContainerDto.class);
	}

	public UserSocialFriendsContainer getSocialFriendsForProviderResult(ExternalProviderType type, Integer offset, Integer limit) {
		User currentUser = userService.getCurrentUser();
		UserSocialFriendsContainer container = getFriendsForSocialNetwork(currentUser, type, offset, limit);
		if (container == null)
			container = new UserSocialFriendsContainer();

		return container;
	}

	private UserSocialFriendsContainer getFriendsForSocialNetwork(User currentUser, ExternalProviderType type, Integer offset, Integer limit) {
		offset = offset != null ? Math.abs(offset) : 0;
		limit = limit != null ? Math.abs(limit) : PagingService.PAGE_SIZE_DEFAULT_VALUE;

		// Guard against malicious input.
		if (limit > PagingService.PAGE_SIZE_MAX_VALUE)
			limit = PagingService.PAGE_SIZE_MAX_VALUE;

		String userSocialId = currentUser.getMetadataItems().get(String.format(UserService.EXTERNAL_PROVIDER_ID_KEY, type.name()));

		if (userSocialId != null) { // Check if the user has linked the current provider.
			try {
				if (type == ExternalProviderType.FACEBOOK) { // Remove this restriction to allow support for other external providers.
					UserSocialFriendsContainer container = new UserSocialFriendsContainer();
					container.setExternalProviderType(type);
					container.setUsers(facebookService.getFriendsList(currentUser, offset, limit));
					container.setLimit(limit);
					container.setOffset(offset);
					container.setResultsCount(container.getUsers().size());
					return container;
				}
			} catch (SocialNetworkException e) {
				log.error(e);
			}
		}

		return null;
	}
}

package com.graffitab.server.persistence.model.user;

import java.util.ArrayList;
import java.util.List;

import com.graffitab.server.api.dto.user.ExternalProviderDto.ExternalProviderType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserSocialFriendsContainer {

	private ExternalProviderType externalProviderType;
	private List<User> users = new ArrayList<>();
	private Integer resultsCount;
	private Integer offset;
	private Integer limit;
}

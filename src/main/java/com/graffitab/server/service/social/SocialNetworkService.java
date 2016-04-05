package com.graffitab.server.service.social;

import java.net.URL;
import java.util.List;

import org.javatuples.Pair;

import com.graffitab.server.persistence.model.user.User;

public interface SocialNetworkService {

	List<User> getFriendsList(User user, Integer offset, Integer limit);
	URL getProfilePictureUrl(User user, int width, int height);
	Pair<String, String> fetchExternalProviderMetadata(User user);
}


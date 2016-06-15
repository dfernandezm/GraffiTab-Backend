package com.graffitab.server.service.social;

import java.net.URL;
import java.util.List;

import com.graffitab.server.persistence.model.user.User;

public interface SocialNetworkService {

	List<User> getFriendsList(User user, Integer offset, Integer limit);
	URL getProfilePictureUrl(User user, int width, int height);
	boolean isValidToken(String accessToken);
}


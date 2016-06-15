package com.graffitab.server.service.social;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.graffitab.server.persistence.model.externalprovider.ExternalProvider;
import com.graffitab.server.persistence.model.externalprovider.ExternalProviderType;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.service.TransactionUtils;
import com.graffitab.server.service.user.ExternalProviderService;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Friend;
import facebook4j.Reading;
import facebook4j.auth.AccessToken;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class FacebookSocialNetworkService implements SocialNetworkService {

	@Resource
	private TransactionUtils transactionUtils;

	@Resource
	private ExternalProviderService externalProviderService;

	private Facebook facebook;
	private ExternalProviderType providerType = ExternalProviderType.FACEBOOK;

	private String FACEBOOK_APP_ID_ENVVAR_NAME = "FACEBOOK_APP_ID";
	private String FACEBOOK_APP_SECRET_ENVVAR_NAME = "FACEBOOK_APP_SECRET";

	@PostConstruct
	public void setupFacebook() {
		String appId = System.getenv(FACEBOOK_APP_ID_ENVVAR_NAME);
		String appSecret = System.getenv(FACEBOOK_APP_SECRET_ENVVAR_NAME);

		if (StringUtils.hasText(appId) && StringUtils.hasText(appSecret)) {
			if (log.isDebugEnabled()) {
				log.debug("Setting up Facebook with APP ID: " + appId + " and APP SECRET: " + appSecret);
			}
			facebook = new FacebookFactory().getInstance();
			facebook.setOAuthAppId(appId, appSecret);
			facebook.setOAuthPermissions("public_profile, email, user_friends");
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Facebook App ID or secret are not set -- getting social friends for Facebook won't work");
			}
		}
	}

	@SuppressWarnings("unused")
	@Override
	public List<User> getFriendsList(User user, Integer offset, Integer limit) {
		if (log.isDebugEnabled()) {
			log.debug("[FACEBOOK] About to fetch user friends");
		}

		ExternalProvider externalProvider = externalProviderService.findExternalProvider(user, providerType);
		String userId = externalProvider.getExternalUserId();
		String userAccessToken = externalProvider.getAccessToken();

		try {
			// At this point we should have the access token and user ID.
			AccessToken extendedToken = facebook.extendTokenExpiration(userAccessToken);
			facebook.setOAuthAccessToken(extendedToken);

			// Use the authenticated user to obtain a list of Facebook friends that have granted access to the app.
			List<Friend> friends = facebook.getFriends(new Reading().limit(limit).offset(offset));
			List<User> graffiTabUsers = new ArrayList<>();

			friends.forEach(friend -> { // Check if the Facebook user is actually registered first.
				if (log.isDebugEnabled()) {
					log.debug("[FACEBOOK] Checking friend with id " + friend.getId() + " and name " + friend.getName());
				}

				User graffitabUser = transactionUtils.executeInTransactionWithResult(() -> {
					return externalProviderService.findUserWithExternalProvider(providerType, friend.getId());
				});
				if (graffitabUser != null) { // User is registered in our database, so safely return them at this point.
					graffiTabUsers.add(graffitabUser);
				}
				else if (log.isDebugEnabled()) {
					log.debug("[FACEBOOK] Friend does not exist in our database. Ignoring for now..");
				}
			});

			if (log.isDebugEnabled()) {
				log.debug("[FACEBOOK] Finished fetching user friends");
			}

			return graffiTabUsers;
		} catch (FacebookException e) {
			String msg = "Error fetching user friends";
			log.error(msg, e);
			throw new SocialNetworkException(msg, e);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public URL getProfilePictureUrl(User user, int width, int height) {
		if (log.isDebugEnabled()) {
			log.debug("[FACEBOOK] About to fetch user profile picture");
		}

		ExternalProvider externalProvider = externalProviderService.findExternalProvider(user, providerType);
		String userId = externalProvider.getExternalUserId();
		String userAccessToken = externalProvider.getAccessToken();

		try {
			// At this point we should have the access token and user ID.
			AccessToken extendedToken = facebook.extendTokenExpiration(userAccessToken);
			facebook.setOAuthAccessToken(extendedToken);

			URL pictureUrl = facebook.getPictureURL(width, height);

			if (log.isDebugEnabled()) {
				log.debug("[FACEBOOK] Finished fetching user profile picture");
			}

			return pictureUrl;
		} catch (FacebookException e) {
			String msg = "Error fetching user profile picture";
			log.error(msg, e);
			throw new SocialNetworkException(msg, e);
		}
	}

	@Override
	public boolean isValidToken(String accessToken) {
		HttpURLConnection conn = null;
	    try {
	    	String url = "https://graph.facebook.com/me?access_token=" + accessToken;
	        conn = (HttpURLConnection) new URL(url).openConnection();
	        conn.setRequestMethod("HEAD");
	        conn.getInputStream();
	        return conn.getResponseCode() == 200; // We have a valid access code.
	    } catch (IOException e) {
	    	String msg = "Error validating access token";
			log.error(msg, e);
	    } finally {
	    	if (conn != null)
	    		conn.disconnect();
	    }
	    return false;
	}
}

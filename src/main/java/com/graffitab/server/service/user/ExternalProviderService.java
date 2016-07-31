package com.graffitab.server.service.user;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.externalprovider.ExternalProvider;
import com.graffitab.server.persistence.model.externalprovider.ExternalProviderType;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.service.social.SocialNetworksService;

@Service
public class ExternalProviderService {

    @Resource
    private UserService userService;

    @Resource
	private HibernateDaoImpl<User, Long> userDao;

    @Resource
    private HibernateDaoImpl<ExternalProvider, Long> externalProviderDao;

    @Resource
    private SocialNetworksService socialNetworksService;

    @Transactional
    public User linkExternalProvider(ExternalProviderType externalProviderType, String externalUserId, String accessToken) {
        User currentUser = userService.getCurrentUser();
        userService.merge(currentUser);
        ExternalProvider externalProvider = findExternalProvider(currentUser, externalProviderType);

        // Check if an external provider already exists.
        if (externalProvider != null) {
            throw new RestApiException(ResultCode.ALREADY_EXISTS, "An external provider for user " + currentUser.getUsername() + " with externalUserId " + externalUserId + " already exists for type " + externalProviderType.name());
        }

        // Check if access token is valid.
		if (socialNetworksService.isValidToken(accessToken, externalProviderType)) {
			currentUser.getExternalProviders().add(ExternalProvider.provider(externalProviderType, externalUserId, accessToken));
		}
		else {
			throw new RestApiException(ResultCode.INVALID_TOKEN,
					"The provided token is not valid.");
		}

		return currentUser;
    }

    @Transactional
    public User unlinkExternalProvider(ExternalProviderType externalProviderType) {
        User currentUser = userService.getCurrentUser();
        userService.merge(currentUser);
        ExternalProvider externalProvider = findExternalProvider(currentUser, externalProviderType);

        // Check if an external provider of that type exists for the user.
        if (externalProvider == null) {
            throw new RestApiException(ResultCode.EXTERNAL_PROVIDER_NOT_FOUND, "An external provider with type " + externalProviderType.name() + " does not exist for user " + currentUser.getUsername());
        }

        currentUser.getExternalProviders().remove(externalProvider);

        return currentUser;
    }

    @Transactional
    public void updateToken(User user, ExternalProviderType externalProviderType, String accessToken) {
    	ExternalProvider externalProvider = findExternalProvider(user, externalProviderType);
		externalProvider.setAccessToken(accessToken);
	}

    public ExternalProvider findExternalProvider(ExternalProviderType externalProviderType, String externalUserId, String accessToken) {
        Query query = externalProviderDao.createNamedQuery("ExternalProvider.findExternalProvider");
        query.setParameter("externalProviderType", externalProviderType);
        query.setParameter("externalUserId", externalUserId);
        query.setParameter("accessToken", accessToken);
        return (ExternalProvider) query.uniqueResult();
    }

    public ExternalProvider findExternalProvider(ExternalProviderType externalProviderType, String externalUserId) {
        Query query = externalProviderDao.createNamedQuery("ExternalProvider.findExternalProviderWithoutAccessToken");
        query.setParameter("externalProviderType", externalProviderType);
        query.setParameter("externalUserId", externalUserId);
        return (ExternalProvider) query.uniqueResult();
    }

    public ExternalProvider findExternalProvider(User user, ExternalProviderType externalProviderType) {
        Query query = userDao.createNamedQuery("User.findExternalProviderForUser");
        query.setParameter("externalProviderType", externalProviderType);
        query.setParameter("currentUser", user);
        return (ExternalProvider) query.uniqueResult();
    }

    public User findUserWithExternalProvider(ExternalProviderType externalProviderType, String externalUserId) {
		Query query = userDao.createNamedQuery("User.findUserWithExternalProvider");
		query.setParameter("externalProviderType", externalProviderType);
		query.setParameter("externalUserId", externalUserId);
		return (User) query.uniqueResult();
	}
}

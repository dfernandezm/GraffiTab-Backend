package com.graffitab.server.service.user;

import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.api.dto.ListItemsResult;
import com.graffitab.server.api.dto.user.ExternalProviderDto.ExternalProviderType;
import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.api.errors.EntityNotFoundException;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.api.errors.UserNotLoggedInException;
import com.graffitab.server.api.errors.ValidationErrorException;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.PagedList;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.User.AccountStatus;
import com.graffitab.server.persistence.model.asset.Asset;
import com.graffitab.server.persistence.model.asset.Asset.AssetType;
import com.graffitab.server.service.PagingService;
import com.graffitab.server.service.TransactionUtils;
import com.graffitab.server.service.email.EmailService;
import com.graffitab.server.service.notification.NotificationService;
import com.graffitab.server.service.store.DatastoreService;
import com.graffitab.server.util.GuidGenerator;
import com.graffitab.server.util.PasswordGenerator;

import lombok.extern.log4j.Log4j2;

/**
 * Created by david
 */
@Service
@Log4j2
public class UserService {

	@Resource
	private HibernateDaoImpl<User, Long> userDao;

	@Resource
	private UserSessionService userSessionService;

	@Resource
	private DatastoreService datastoreService;

	@Resource
	private UserValidationService validationService;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Resource
	private TransactionUtils transactionUtils;

	@Resource
	private EmailService emailService;

	@Resource
	private NotificationService notificationService;

	@Resource
	private OrikaMapper mapper;

	@Resource
	private HttpServletRequest request;

	@Resource
	private PagingService pagingService;

	public static final String ACTIVATION_TOKEN_METADATA_KEY = "activationToken";
	public static final String ACTIVATION_TOKEN_DATE_METADATA_KEY = "activationTokenDate";
	public static final String RESET_PASSWORD_ACTIVATION_TOKEN = "resetPasswordToken";
	public static final String RESET_PASSWORD_ACTIVATION_TOKEN_DATE = "resetPasswordTokenDate";
	public static final String EXTERNAL_PROVIDER_ID_KEY = "%s_externalId";
	public static final String EXTERNAL_PROVIDER_TOKEN_KEY = "%s_externalToken";

	// 6 hours in milliseconds.
	public static final Long ACTIVATION_TOKEN_EXPIRATION_MS = 6 * 60 * 60 * 1000L;
	// 30 minutes
	public static final Long RESET_PASSWORD_TOKEN_EXPIRATION_MS = 30 * 60 * 1000L;

	@Transactional(readOnly = true)
	public User getUser(Long id) {
		User user = findUserById(id);

		if (user != null) {
			throw new EntityNotFoundException(ResultCode.USER_NOT_FOUND, "Could not find user with id " + id);
		}

		return user;
	}

	@Transactional(readOnly = true)
	public User getUserProfile(Long id) {
		User user = findUserById(id);

		if (user != null) {
			return user;
		} else {
			throw new EntityNotFoundException(ResultCode.NOT_FOUND, "Could not find user with id " + id);
		}
	}

	@Transactional(readOnly = true)
	public User getCurrentUser() {
		// Cache this in a ThreadLocal
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() != null) {
			User currentUser = (User) auth.getPrincipal();
			currentUser = findUserById(currentUser.getId());
			return currentUser;
		} else {
			String msg = "Cannot get logged in user";
			throw new UserNotLoggedInException(msg);
		}
	}

	@Transactional(readOnly = true)
	public User getUserByUsername(String username) {
		try {
			User user = (User) findUserByUsername(username);

			return user;
		} catch (UsernameNotFoundException e) {
			throw new EntityNotFoundException(ResultCode.USER_NOT_FOUND, "Could not find user " + username);
		}
	}

	@Transactional
	public void linkExternalProvider(String externalProviderId, String externalProviderToken,
			ExternalProviderType externalProviderType) {
		User user = findUsersWithMetadataValues(String.format(EXTERNAL_PROVIDER_ID_KEY, externalProviderType.name()),
				externalProviderToken);
		User currentUser = getCurrentUser();

		// Check if a user with that externalId already exists.
		if (user != null) {
			throw new RestApiException(ResultCode.ALREADY_EXISTS,
					"A user with externalId " + externalProviderId + " already exists");
		}

		currentUser.getMetadataItems().put(String.format(EXTERNAL_PROVIDER_ID_KEY, externalProviderType.name()),
				externalProviderId);
		currentUser.getMetadataItems().put(String.format(EXTERNAL_PROVIDER_TOKEN_KEY, externalProviderType.name()),
				externalProviderToken);
	}

	@Transactional
	public void unlinkExternalProvider(ExternalProviderType externalProviderType) {
		User currentUser = getCurrentUser();

		// User can only have 1 instance of a provider associated with their account, so delete it if it's found.
		currentUser.getMetadataItems().remove(String.format(EXTERNAL_PROVIDER_ID_KEY, externalProviderType.name()));
		currentUser.getMetadataItems().remove(String.format(EXTERNAL_PROVIDER_TOKEN_KEY, externalProviderType.name()));
	}

	@Transactional
	public User verifyExternalProvider(String externalId, String accessToken,
			ExternalProviderType externalProviderType) {
		User user = findUsersWithMetadataValues(String.format(EXTERNAL_PROVIDER_ID_KEY, externalProviderType.name()),
				externalId);

		if (user == null) {
			throw new EntityNotFoundException(ResultCode.NOT_FOUND,
					"Could not find user with externalId " + externalId);
		}

		// The user is effectively authenticated using ExternalProviderAuthenticationFilter

		return user;
	}

	@Transactional
	public User activateUser(String token) {
		User user = findUsersWithMetadataValues(ACTIVATION_TOKEN_METADATA_KEY, token);

		if (user == null) {
			throw new EntityNotFoundException(ResultCode.NOT_FOUND, "Could not find token " + token);
		}

		Long tokenDate = Long.parseLong(user.getMetadataItems().get(ACTIVATION_TOKEN_DATE_METADATA_KEY));
		Long now = System.currentTimeMillis();

		if ((now - tokenDate) > ACTIVATION_TOKEN_EXPIRATION_MS) {
			throw new RestApiException(ResultCode.TOKEN_EXPIRED, "This token has expired.");
		}

		if (user.getAccountStatus() != AccountStatus.PENDING_ACTIVATION) {
			throw new RestApiException(ResultCode.USER_NOT_IN_EXPECTED_STATE, "Current user is not in the expected state " +
					AccountStatus.PENDING_ACTIVATION.name());
		}

		user.setAccountStatus(AccountStatus.ACTIVE);

		// Send notification.
		notificationService.addWelcomeNotification(user);

		return user;
	}

	public User createUser(User user, String userToken) {
		if (validationService.validateUser(user)) {
			if (user.getId() == null) {
				transactionUtils.executeInTransaction(() -> {
					user.setPassword(passwordEncoder.encode(user.getPassword()));
					user.setGuid(GuidGenerator.generate());
					user.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
					user.getMetadataItems().put(ACTIVATION_TOKEN_METADATA_KEY, userToken);
					user.getMetadataItems().put(ACTIVATION_TOKEN_DATE_METADATA_KEY, System.currentTimeMillis() + "");
					userDao.persist(user);
				});

				emailService.sendWelcomeEmail(user.getUsername(), user.getEmail(),
						generateUserAccountActivationLink(userToken));

				return user;
			} else {
				throw new RestApiException(ResultCode.BAD_REQUEST,
						"ID has been provided to create endpoint -- This is not allowed");
			}
		} else {
			throw new ValidationErrorException("Validation error creating user");
		}
	}

	public User createExternalUser(User user, final String externalProviderId, String externalProviderToken,
			ExternalProviderType externalProviderType) {
		if (validationService.validateUser(user)) {
			if (user.getId() == null) {
				transactionUtils.executeInNewTransaction(() -> {
					user.setPassword(passwordEncoder.encode(PasswordGenerator.generatePassword()));
					user.setGuid(GuidGenerator.generate());
					user.setAccountStatus(AccountStatus.ACTIVE);
					user.getMetadataItems().put(String.format(EXTERNAL_PROVIDER_ID_KEY, externalProviderType.name()),
							externalProviderId);
					user.getMetadataItems().put(String.format(EXTERNAL_PROVIDER_TOKEN_KEY, externalProviderType.name()),
							externalProviderToken);
					userDao.persist(user);

					// Send notification.
					notificationService.addWelcomeNotification(user);
				});

				emailService.sendWelcomeExternalEmail(user.getUsername(), user.getEmail());

				return user;
			} else {
				throw new RestApiException(ResultCode.BAD_REQUEST,
						"ID has been provided to create endpoint -- This is not allowed");
			}
		} else {
			throw new ValidationErrorException("Validation error creating user");
		}
	}

	@Transactional
	public User updateUser(User user) {
		if (validationService.validateUser(user)) {
			User currentUser = getCurrentUser();
			mapper.map(user, currentUser);

			return currentUser;

		} else {
			throw new ValidationErrorException("Validation error updating user");
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public PagedList<User> searchUser(String query, Integer offset, Integer count) {
		// By username, first name, lastName
		Criterion usernameRestriction = Restrictions.like("username", query, MatchMode.ANYWHERE);
		Criterion firstNameRestriction = Restrictions.like("firstName", query, MatchMode.ANYWHERE);
		Criterion lastNameRestriction = Restrictions.like("lastName", query, MatchMode.ANYWHERE);
		Criterion orRestriction = Restrictions.or(usernameRestriction, firstNameRestriction, lastNameRestriction);

		Integer total = (Integer) userDao.getSession().createCriteria(User.class).add(orRestriction)
				.setProjection(Projections.rowCount()).uniqueResult();

		List<User> listUsers = (List<User>) userDao.getSession().createCriteria(User.class).add(orRestriction)
				.setFirstResult(offset).setMaxResults(count).list();

		return new PagedList<User>(listUsers, total, offset);
	}

	public Asset updateAvatar(InputStream assetInputStream, long contentLength) {
		Asset assetToAdd = Asset.asset(AssetType.IMAGE);

		User user = transactionUtils.executeInTransactionWithResult(() -> {
			User currentUser = getCurrentUser();
			return currentUser;
		});

		datastoreService.saveAsset(assetInputStream, contentLength, assetToAdd.getGuid());

		// Delete current avatar from store, if it exists.
		if (user.getAvatarAsset() != null) {
			datastoreService.deleteAsset(user.getAvatarAsset().getGuid());
		}

		transactionUtils.executeInTransaction(() -> {
			User currentUser = getCurrentUser();

			// Delete current avatar from database, if it exists.
			if (currentUser.getAvatarAsset() != null) {
				currentUser.setAvatarAsset(null);
			}

			currentUser.setAvatarAsset(assetToAdd);
		});

		return assetToAdd;
	}

	public void deleteAvatar() {
		User user = transactionUtils.executeInTransactionWithResult(() -> {
			User currentUser = getCurrentUser();
			return currentUser;
		});

		// Delete current avatar from store, if it exists.
		if (user.getAvatarAsset() != null) {
			datastoreService.deleteAsset(user.getAvatarAsset().getGuid());
		}

		transactionUtils.executeInTransaction(() -> {
			User currentUser = getCurrentUser();

			// Delete current avatar from database, if it exists.
			if (currentUser.getAvatarAsset() != null) {
				currentUser.setAvatarAsset(null);
			}
		});
	}

	public Asset updateCover(InputStream assetInputStream, long contentLength) {
		Asset assetToAdd = Asset.asset(AssetType.IMAGE);

		User user = transactionUtils.executeInTransactionWithResult(() -> {
			User currentUser = getCurrentUser();
			return currentUser;
		});

		datastoreService.saveAsset(assetInputStream, contentLength, assetToAdd.getGuid());

		// Delete current cover from store, if it exists.
		if (user.getCoverAsset() != null) {
			datastoreService.deleteAsset(user.getCoverAsset().getGuid());
		}

		transactionUtils.executeInTransaction(() -> {
			User currentUser = getCurrentUser();

			// Delete current cover from database, if it exists.
			if (currentUser.getCoverAsset() != null) {
				currentUser.setCoverAsset(null);
			}

			currentUser.setCoverAsset(assetToAdd);
		});

		return assetToAdd;
	}

	public void deleteCover() {
		User user = transactionUtils.executeInTransactionWithResult(() -> {
			User currentUser = getCurrentUser();
			return currentUser;
		});

		// Delete current cover from store, if it exists.
		if (user.getCoverAsset() != null) {
			datastoreService.deleteAsset(user.getCoverAsset().getGuid());
		}

		transactionUtils.executeInTransaction(() -> {
			User currentUser = getCurrentUser();

			// Delete current cover from database, if it exists.
			if (currentUser.getCoverAsset() != null) {
				currentUser.setCoverAsset(null);
			}
		});
	}

	@Transactional
	public ListItemsResult<UserDto> getFollowingOrFollowersResultForUser(boolean shouldGetFollowers, Long userId, Integer offset, Integer count) {
		User requestedUser = (userId == null) ? getCurrentUser() : findUserById(userId);

		Query query = userDao.createQuery(
				"select f "
			  + "from User u "
			  + "join u." + (shouldGetFollowers ? "followers" : "following") + " f "
			  + "where u = :currentUser");
		query.setParameter("currentUser", requestedUser);

		return pagingService.getPagedItemsResult(User.class, UserDto.class, offset, count, query);
	}

	@Transactional
	public User follow(Long toFollowId) {
		User toFollow = findUserById(toFollowId);

		if (toFollow != null) {
			User currentUser = getCurrentUser();

			// Users can't follow themselves.
			if (currentUser.equals(toFollow)) {
				throw new RestApiException(ResultCode.BAD_REQUEST, "You cannot follow yourself");
			}

			if (!currentUser.isFollowing(toFollow)) {
				currentUser.getFollowing().add(toFollow);

				// Send notification.
				notificationService.addFollowNotification(toFollow, currentUser);
			}

			return toFollow;
		} else {
			throw new RestApiException(ResultCode.USER_NOT_FOUND, "User with id " + toFollowId + " not found");
		}
	}

	@Transactional
	public User unfollow(Long toUnfollowId) {
		User toUnfollow = findUserById(toUnfollowId);

		if (toUnfollow != null) {
			User currentUser = getCurrentUser();

			currentUser.getFollowing().remove(toUnfollow);

			return toUnfollow;
		} else {
			throw new RestApiException(ResultCode.USER_NOT_FOUND, "User with id " + toUnfollowId + " not found");
		}
	}

	public User resetPassword(String email) {
		final String resetPasswordToken = GuidGenerator.generate();
		User user = transactionUtils.executeInTransactionWithResult(() -> {
			User innerUser = findByEmail(email);

			if (innerUser == null) {
				throw new EntityNotFoundException(ResultCode.USER_NOT_FOUND,
						"User with email: " + email + " not found");
			}

			innerUser.setAccountStatus(AccountStatus.RESET_PASSWORD);
			innerUser.getMetadataItems().put(RESET_PASSWORD_ACTIVATION_TOKEN, resetPasswordToken);
			innerUser.getMetadataItems().put(RESET_PASSWORD_ACTIVATION_TOKEN_DATE, System.currentTimeMillis() + "");
			return innerUser;
		});

		emailService.sendResetPasswordEmail(email, generateResetPasswordLink(resetPasswordToken));

		return user;
	}

	@Transactional
	public User completePasswordReset(String token, String newPassword) {

		User user = findUsersWithMetadataValues(RESET_PASSWORD_ACTIVATION_TOKEN, token);

		if (user == null) {
			throw new EntityNotFoundException(ResultCode.NOT_FOUND, "Could not find token " + token);
		}

		Long tokenDate = Long.parseLong(user.getMetadataItems().get(RESET_PASSWORD_ACTIVATION_TOKEN_DATE));
		Long now = System.currentTimeMillis();

		if ((now - tokenDate) > RESET_PASSWORD_TOKEN_EXPIRATION_MS) {
			throw new RestApiException(ResultCode.TOKEN_EXPIRED, "This token has expired.");
		}

		if (user.getAccountStatus() != AccountStatus.RESET_PASSWORD) {
			throw new RestApiException(ResultCode.USER_NOT_IN_EXPECTED_STATE, "Current user is not in the expected state " +
					AccountStatus.RESET_PASSWORD.name());
		}

		user.setAccountStatus(AccountStatus.ACTIVE);
		user.setPassword(passwordEncoder.encode(newPassword));

		// Logout from all devices
		userSessionService.logoutEverywhere(user);

		if (log.isDebugEnabled()) {
			log.debug("Successfully reset password for user " + user.getUsername());
		}

		return user;
	}

	@Transactional
	public User changePassword(String currentPassword, String newPassword) {

		User user = getCurrentUser();

		// Check if the provided password matches the current password.
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new RestApiException(ResultCode.INCORRECT_PASSWORD, "The provided password was incorrect.");
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		// Logout from all devices
        userSessionService.logoutEverywhere(user);

		if (log.isDebugEnabled()) {
			log.debug("Successfully changed password for user " + user.getUsername());
		}

		return user;
	}

	@Transactional(readOnly = true)
	public User findUserById(Long id) {
		return userDao.find(id);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> findAll() {
		Query query = userDao.createNamedQuery("User.findAll");
		return (List<User>) query.list();
	}

	@Transactional(readOnly = true)
	public UserDetails findUserByUsername(String username) throws UsernameNotFoundException {

		UserDetails userDetails = (UserDetails) findByUsername(username);

		if (userDetails == null) {
			throw new UsernameNotFoundException("The user " + username + " was not found");
		}

		return userDetails;
	}

	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		Criteria criteria = userDao.getBaseCriteria();
		User user = (User) criteria.add(Restrictions.eq("username", username)).uniqueResult();
		return user;
	}

	@Transactional(readOnly = true)
	public User findByEmail(String email) {

		User user = (User) userDao.getBaseCriteria().add(Restrictions.eq("email", email)).uniqueResult();
		return user;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> findUsersByUsernameWithDifferentId(String username, Long userId) {
		Query query = userDao.createNamedQuery("User.findUsersWithUsername");
		query.setParameter("username", username);
		query.setParameter("userId", userId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> findUsersByEmailWithDifferentID(String email, Long userId) {
		Query query = userDao.createNamedQuery("User.findUsersWithEmail");
		query.setParameter("email", email);
		query.setParameter("userId", userId);
		return query.list();
	}

	private User findUsersWithMetadataValues(String key, String value) {
		Query query = userDao.createNamedQuery("User.findUsersWithMetadataValues");
		query.setParameter("metadataKey", key);
		query.setParameter("metadataValue", value);
		return (User) query.uniqueResult();
	}

	/**
	 * Returns the base server link i.e. http://www.graffitab.com:port
	 *
	 * @return
	 */
	private String generateBaseLink() {
		return request.getScheme() + "://" + request.getServerName()
				+ ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
	}

	private String generateResetPasswordLink(String resetPasswordToken) {
		return generateBaseLink() + "/api/users/resetpassword/" + resetPasswordToken;
	}

	private String generateUserAccountActivationLink(String userToken) {
		String activationLink = generateBaseLink() + "/api/users/activate/" + userToken;
		return activationLink;
	}
}

package com.graffitab.server.service;

import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.graffitab.server.api.errors.EntityNotFoundException;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.api.errors.UserNotLoggedInException;
import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.Asset;
import com.graffitab.server.persistence.model.Asset.AssetType;
import com.graffitab.server.persistence.model.PagedList;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.User.AccountStatus;
import com.graffitab.server.service.email.EmailService;
import com.graffitab.server.service.store.DatastoreService;
import com.graffitab.server.util.GuidGenerator;

/**
 * Created by david
 */
@Service
public class UserService {

	private static Logger log = LogManager.getLogger();

	@Resource
	private HibernateDaoImpl<User, Long> userDao;

	@Resource
	private PagingService<User> pagingService;

	@Resource
	private DatastoreService datastoreService;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Resource
	private TransactionUtils transactionUtils;

	@Resource
	private EmailService emailService;

	@Resource
	private HttpServletRequest request;

	public static final String METADATA_KEY_ACTIVATION_TOKEN = "activationToken";
	public static final String METADATA_KEY_ACTIVATION_TOKEN_DATE = "activationTokenDate";

	// 6 hours in milliseconds.
	public static final Long TOKEN_EXPIRATION_MS = 6 * 60 * 60 * 1000L;

	@Transactional(readOnly = true)
	public UserDetails findUserByUsername(String username) throws UsernameNotFoundException {
		Criteria criteria = userDao.getBaseCriteria();
		UserDetails userDetails =
				(UserDetails) criteria.add(Restrictions.eq("username", username)).uniqueResult();

		if (userDetails == null) {
			throw new UsernameNotFoundException("The user " + username + " was not found");
		}

		return userDetails;
	}

	@Transactional
	public User activateUserWithToken(String token) {
		User user = findUsersWithToken(token);

		if (user == null) {
			throw new EntityNotFoundException(ResultCode.NOT_FOUND, "Could not find token " + token);
		}

		Long tokenDate = Long.parseLong(user.getMetadataItems().get(METADATA_KEY_ACTIVATION_TOKEN_DATE));
		Long now = System.currentTimeMillis();

		if ((Long)(now - tokenDate) > TOKEN_EXPIRATION_MS) {
			throw new RestApiException(ResultCode.TOKEN_EXPIRED, "This token has expired.");
		}

		user.setAccountStatus(AccountStatus.ACTIVE);

		userDao.merge(user);

		return user;
	}

	@Transactional
	public void saveUser(User user) {
		final String userToken = GuidGenerator.generate();

		transactionUtils.executeInNewTransaction(() -> {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setGuid(GuidGenerator.generate());
			user.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
			user.getMetadataItems().put(METADATA_KEY_ACTIVATION_TOKEN, userToken);
			user.getMetadataItems().put(METADATA_KEY_ACTIVATION_TOKEN_DATE, System.currentTimeMillis() + "");
			userDao.persist(user);
		});

		emailService.prepareAndSendWelcomeEmail(user.getUsername(), user.getEmail(), generateUserAccountActivationLink(userToken));
	}

	private String generateUserAccountActivationLink(String userToken) {
		String activationLink = request.getScheme() + "://" +
	             request.getServerName() +
	             ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "") +
	             "/api/users/activate/" + userToken;
		return activationLink;
	}

	@Transactional(readOnly = true)
	public User findUserById(Long id) {
		return userDao.find(id);
	}

	@Transactional
	public void merge(User user) {
		userDao.merge(user);
	}

	@Transactional
	public void remove(Long userId) {
		userDao.remove(userId);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> findAll() {
		Query query = userDao.createNamedQuery("User.findAll");
		return (List<User>) query.list();
	}

	@Transactional(readOnly = true)
	public List<User> findByUsername(String username) {
		return userDao.findByField("username", username);
	}

	@Transactional(readOnly = true)
	public List<User> findByEmail(String email) {
		return userDao.findByField("email", email);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> findUsersWithUsername(String username, Long userId) {
		Query query = userDao.createNamedQuery("User.findUsersWithUsername");
		query.setParameter("username", username);
		query.setParameter("userId", userId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> findUsersWithEmail(String email, Long userId) {
		Query query = userDao.createNamedQuery("User.findUsersWithEmail");
		query.setParameter("email", email);
		query.setParameter("userId", userId);
		return query.list();
	}

	@Transactional(readOnly = true)
	public User findUsersWithToken(String token) {
		Query query = userDao.createNamedQuery("User.findUsersWithToken");
		query.setParameter("tokenKeyName", METADATA_KEY_ACTIVATION_TOKEN);
		query.setParameter("token", token);
		return (User) query.uniqueResult();
	}

	// Temporary method
	@Transactional
	public void addFollowerToUser(Long currentUserId, Long followerId) {
		User current = findUserById(currentUserId);
		User follower = findUserById(followerId);
		current.getFollowers().add(follower);
	}

	@Transactional
	public void flush() {
		userDao.flush();
	}

	@SuppressWarnings("unchecked")
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


	public Asset addAssetToCurrentUser(InputStream assetInputStream, AssetType assetType, long contentLength) {

		Asset assetToAdd = Asset.asset(assetType);

		//TODO: Bring back this when we have new AWS Keys
		// datastoreService.saveAsset(assetInputStream, contentLength, user.getGuid(), assetToAdd.getGuid(), assetType, null);

		Asset addedAsset = transactionUtils.executeInTransactionWithResult(() -> {
			User currentUser = getCurrentUser();
			currentUser.getAssets().add(assetToAdd);
			return assetToAdd;
		});

		return addedAsset;
	}

	@Transactional
	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() != null) {
			User currentUser = (User) auth.getPrincipal();
			currentUser = userDao.merge(currentUser);
			return currentUser;
		} else {
			String msg = "Cannot get logged in user";
			throw new UserNotLoggedInException(msg);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public PagedList<User> getFollowingOrFollowers(boolean shouldGetFollowers, Long userId, Integer offset, Integer count) {
		User user = userId == null ? getCurrentUser() : findUserById(userId);

		Query query = userDao.createQuery("select f from User u join u." + (shouldGetFollowers ? "followers" : "following") + " f where u = :currentUser");
		query.setParameter("currentUser", user);

		query.setFirstResult(offset != null ? offset : 0);
		query.setMaxResults(count != null ? count : 10);

		PagedList<User> users = new PagedList<>((List<User>)query.list(), offset, count);

		User currentUser = getCurrentUser();

		// Check if the current user is following user u from the list.
		users.forEach(u -> u.setFollowedByCurrentUser(currentUser.getFollowing().contains(u)));

		return users;
	}

// TODO: Try to get it to work with Criteria
//	Criteria criteria = userDao.getBaseCriteria("u");
//	criteria.createAlias("u.followers", "f")
//			.add(Restrictions.eq("u.id", getCurrentUser().getId())).setProjection(Projections.);
//	PagedList<User> followers = userDao.findPaged(criteria, offset, count);

/*
 List results = session.createCriteria(Domestic.class, "cat")
  .createAlias("kittens", "kit")
  .setProjection( Projections.projectionList()
    .add( Projections.property("cat.name"), "catName" )
    .add( Projections.property("kit.name"), "kitName" )
)
.addOrder( Order.asc("catName") )
.addOrder( Order.asc("kitName") )
.list();
*/
}

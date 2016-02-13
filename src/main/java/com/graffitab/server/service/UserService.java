package com.graffitab.server.service;

import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.AssetType;
import com.graffitab.server.persistence.model.PagedList;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.store.DatastoreService;
import com.graffitab.server.util.GuidGenerator;

/**
 * Created by david
 */
@Service
public class UserService {

	@Resource
	private HibernateDaoImpl<User, Long> userDao;

	@Resource
	private PagingService<User> pagingService;

	@Resource
	private DatastoreService datastoreService;

	@Resource
	private PasswordEncoder passwordEncoder;

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
	public void saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userDao.persist(user);
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

	public void addAssetToUser(InputStream inputStream, AssetType assetType, String userGuid, long contentLength) {
		datastoreService.saveAsset(inputStream, contentLength, userGuid, GuidGenerator.generate(), assetType, null);
	}
}

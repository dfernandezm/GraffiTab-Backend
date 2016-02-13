package com.graffitab.server.service;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.PagedList;
import com.graffitab.server.persistence.model.User;

/**
 * Created by david 
 */
@Service
public class UserService {

 @Resource
 private HibernateDaoImpl<User, Long> userDao;
 
 @Resource
 private PagingService<User> pagingService;

 @Transactional(readOnly=true)
 public User findUserById(Long id) {
	 return userDao.find(id);
 }
 
 @Transactional
 public void persist(User user) {
	 userDao.persist(user);
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
 @Transactional(readOnly=true)
 public List<User> findAll() {
	 Query query = userDao.createNamedQuery("User.findAll");
	 return (List<User>)query.list();
 }
 
 @Transactional(readOnly=true)
 public List<User> findByUsername(String username) {
	 return userDao.findByField("username", username);
 }
 
 @Transactional(readOnly=true)
 public List<User> findByEmail(String email) {
	 return userDao.findByField("email", email);
 }
 
 @SuppressWarnings("unchecked")
 @Transactional(readOnly=true)
 public List<User> findUsersWithUsername(String username, Long userId) {
	 Query query = userDao.createNamedQuery("User.findUsersWithUsername");
	 query.setParameter("username", username);
	 query.setParameter("userId", userId);
	 return query.list();
 }
 
 @SuppressWarnings("unchecked")
 @Transactional(readOnly=true)
 public List<User> findUsersWithEmail(String email, Long userId) {
	 Query query = userDao.createNamedQuery("User.findUsersWithEmail");
	 query.setParameter("email", email);
	 query.setParameter("userId", userId);
	 return query.list();
 }
 

 //Temporary method
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
	 
	 // select count(*)
	 // from gt_user g
	 // where g.username like '%query%'
	 
	 // select g
	 // from gt_user g
	 // where g.username like '%query%'
	 
	 Criterion usernameRestriction = Restrictions.like("username", query, MatchMode.ANYWHERE);
	 Criterion firstNameRestriction = Restrictions.like("firstName", query, MatchMode.ANYWHERE);
	 Criterion lastNameRestriction = Restrictions.like("lastName", query, MatchMode.ANYWHERE);
	 Criterion orRestriction = Restrictions.or(usernameRestriction, firstNameRestriction,
			 								lastNameRestriction);
	  
	 Integer total = (Integer) userDao.getSession()
	 .createCriteria(User.class)
	 .add(orRestriction)
	 .setProjection(Projections.rowCount())
	 .uniqueResult();
	 
	 List<User> listUsers = (List<User>) userDao.getSession()
			 .createCriteria(User.class)
			 .add(orRestriction)
			 .setFirstResult(offset)
			 .setMaxResults(count)
			 .list();
	
	 return new PagedList<User>(listUsers,total,offset); 
 }
 
 
}

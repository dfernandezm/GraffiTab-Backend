package com.graffitab.server.service;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.User;

/**
 * Created by david 
 */
@Service
public class UserService {

 @Resource
 private HibernateDaoImpl<User, Long> userDao;

 @Transactional(readOnly=true)
 public User getUserById(Long id) {
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
 
 
}

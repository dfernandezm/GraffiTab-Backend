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
 public User findByUsername(String username) {
	 //TODO:
	 return null;
 }
 
 @Transactional(readOnly=true)
 public User findByEmail(String email) {
	 //TODO:
	 return null;
 }
 
 
}

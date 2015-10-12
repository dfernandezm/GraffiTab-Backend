package com.graffitab.server.service;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by david 
 */
@Service
public class PersonService {

 @Autowired
 private HibernateDaoImpl<Person, Long> personDao;



}

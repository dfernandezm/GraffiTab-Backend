package com.graffitab.server.service;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.dao.Identifiable;
import com.graffitab.server.persistence.model.PagedList;


// Possibly could be deprecated its use
public class PagingService<T extends Identifiable<Long>> {
	
	public static Integer PAGE_SIZE_DEFAULT_VALUE = 10;
	
	private HibernateDaoImpl<T, Long> dao;
	
	public PagingService(HibernateDaoImpl<T,Long> dao) {
		this.dao = dao;
	}
	
	public PagedList<T> findAllPaged(Integer offset, Integer count) {
		return dao.findAllPaged(offset, count);
   }	
}

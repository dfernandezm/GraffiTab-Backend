package com.graffitab.server.service;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.dao.Identifiable;
import com.graffitab.server.persistence.model.PagedList;

public class PagingService<T extends Identifiable<Long>> {
	
	public static Integer PAGE_SIZE_DEFAULT_VALUE = 10;
	
	private HibernateDaoImpl<T, Long> dao;
	
	public PagingService(HibernateDaoImpl<T,Long> dao) {
		this.dao = dao;
	}
	
	public void getCountQuery(Criteria currentCriteria) {
		currentCriteria.setProjection(Projections.rowCount());
	}
	
	public void getMainQuery(Criteria currentCriteria, Integer offset, Integer count) {
		currentCriteria.setFirstResult(offset).setMaxResults(count);
	}
	
	@SuppressWarnings("unchecked")
	public PagedList<T> findAllPaged(Integer offset, Integer count) {
		
	 offset = (offset == null) ? 0 : offset;
	 count = (count == null) ? PagingService.PAGE_SIZE_DEFAULT_VALUE : count;
	 
	 Criteria countCriteria = dao.getCriteria();
	 Criteria mainCriteria = dao.getCriteria();
	 
	 getCountQuery(countCriteria);
	 Integer total = ((Long) countCriteria.uniqueResult()).intValue();
	 
	 getMainQuery(mainCriteria, offset, count);
	 List<T> results = (List<T>) mainCriteria.list();
	 
	 PagedList<T> listUsers = new PagedList<>(results, total, offset, count);
	 
	 return listUsers;
	 
   }
	
	
}

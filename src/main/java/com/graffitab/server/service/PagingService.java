package com.graffitab.server.service;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Service;

@Service
public class PagingService {
	
	public static Integer PAGE_SIZE_DEFAULT_VALUE = 10;
	
	public void getCountQuery(Criteria currentCriteria) {
		currentCriteria.setProjection(Projections.rowCount());
	}
	
	public void getMainQuery(Criteria currentCriteria, Integer offset, Integer count) {
		currentCriteria.setFirstResult(offset).setMaxResults(count);
	}
	
}

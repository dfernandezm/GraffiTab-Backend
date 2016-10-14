package com.graffitab.server.service.paging;

import java.util.List;

import org.hibernate.Query;

import com.graffitab.server.persistence.model.PagedList;

public class LocationsPagingService extends PagingService {

	@Override
	public <T> PagedList<T> getItems(Query query, Integer offset, Integer limit) {
		offset = offset != null ? Math.abs(offset) : 0;
		limit = limit != null ? Math.abs(limit) : PAGE_SIZE_DEFAULT_VALUE_LOCATION;

		// Guard against malicious input.
		if (limit > PAGE_SIZE_MAX_VALUE_LOCATION)
			limit = PAGE_SIZE_MAX_VALUE_LOCATION;

		query.setFirstResult(offset);
		query.setMaxResults(limit);

		return new PagedList<>((List<T>) query.list(), offset, limit);
	}
}

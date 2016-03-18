package com.graffitab.server.service;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.api.dto.ListItemsResult;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.model.PagedList;

@Service
public class PagingService {

	public static final Integer PAGE_SIZE_DEFAULT_VALUE = 10;
	public static final Integer PAGE_SIZE_MAX_VALUE = 20;

	@Resource
	private OrikaMapper mapper;

	@Transactional
	public <T, K> ListItemsResult<K> getPagedItemsResult(Class<T> targetClass, Class<K> targetDtoClass, Integer offset, Integer count, Query query) {
		offset = offset != null ? offset : 0;
		count = count != null ? count : PAGE_SIZE_DEFAULT_VALUE;

		// Guard against malicious input.
		if (count > PAGE_SIZE_MAX_VALUE)
			count = PAGE_SIZE_MAX_VALUE;

		// Get list of entities.
		PagedList<T> items = getItems(query, offset, count);

		// Map to list of DTOs.
		List<K> itemDtos = mapper.mapList(items, targetDtoClass);

		// Build result list.
		ListItemsResult<K> listItemsResult = new ListItemsResult<>();
		listItemsResult.setItems(itemDtos);
		listItemsResult.setTotal(items.getTotal());
		listItemsResult.setCount(items.getCount());
		listItemsResult.setOffset(items.getOffset());

		return listItemsResult;
	}

	@SuppressWarnings("unchecked")
	private <T> PagedList<T> getItems(Query query, Integer offset, Integer count) {
		query.setFirstResult(offset);
		query.setMaxResults(count);

		return new PagedList<>((List<T>) query.list(), offset, count);
	}
}

package com.graffitab.server.service.paging;

import java.util.List;

import org.hibernate.Query;

public interface PagingServiceQueryProvider<T> {

	Query getItemSearchQuery();
	List<T> getAugmentedItemsList(List<T> items);
}

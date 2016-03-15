package com.graffitab.server.service.paging;

import org.hibernate.Query;

public interface PagingServiceQueryProvider<T> {

	Query getItemSearchQuery();
}

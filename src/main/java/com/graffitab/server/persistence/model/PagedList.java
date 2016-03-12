package com.graffitab.server.persistence.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.service.paging.PagingService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagedList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	@JsonProperty("total")
	private Integer total;

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("count")
	private Integer count = PagingService.PAGE_SIZE_DEFAULT_VALUE;

	public PagedList(List<T> resultsList, Integer offset, Integer count) {
		super(resultsList);

		this.total = resultsList.size();
		this.offset = offset;
		this.count = count;
	}

	public PagedList() {
		super();

		this.total = 0;
		this.offset = 0;
		this.count = PagingService.PAGE_SIZE_DEFAULT_VALUE;
	}
}

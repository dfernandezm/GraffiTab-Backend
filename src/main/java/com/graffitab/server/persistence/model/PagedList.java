package com.graffitab.server.persistence.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.service.paging.PagingService;

@Getter
@Setter
public class PagedList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	@JsonProperty("resultsCount")
	private Integer resultsCount;

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit = PagingService.PAGE_SIZE_DEFAULT_VALUE;

	public PagedList(List<T> resultsList, Integer offset, Integer limit) {
		super(resultsList);

		this.resultsCount = resultsList.size();
		this.offset = offset;
		this.limit = limit;
	}

	public PagedList() {
		super();

		this.resultsCount = 0;
		this.offset = 0;
		this.limit = PagingService.PAGE_SIZE_DEFAULT_VALUE;
	}
}

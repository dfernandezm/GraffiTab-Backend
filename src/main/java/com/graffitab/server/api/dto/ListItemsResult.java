package com.graffitab.server.api.dto;

import java.util.List;

import lombok.Data;

@Data
public class ListItemsResult<T> {

	private List<T> items;
	private Integer resultsCount;
	private Integer offset;
	private Integer limit;
}

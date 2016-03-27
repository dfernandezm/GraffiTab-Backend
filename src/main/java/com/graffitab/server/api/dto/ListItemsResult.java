package com.graffitab.server.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ListItemsResult<T> {

	private List<T> items;
	private Integer resultsCount;
	private Integer offset;
	private Integer limit;
}

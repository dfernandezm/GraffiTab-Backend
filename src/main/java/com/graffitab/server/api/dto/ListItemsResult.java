package com.graffitab.server.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListItemsResult<T> {

	private List<T> items;
	private Integer total;
	private Integer offset;
	private Integer count;
}

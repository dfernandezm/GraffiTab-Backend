package com.graffitab.server.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ListResult<T> {
	private List<T> elements;
	private Integer total;
	private Integer offset;
	private Integer pageSize;
}

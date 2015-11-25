package com.graffitab.server.persistence.model;

import java.util.ArrayList;
import java.util.List;

import com.graffitab.server.service.PagingService;

public class PagedList<T> extends ArrayList<T> {
	
	private static final long serialVersionUID = 1L;
	
	private Integer total;
	private Integer offset;
	private Integer count = PagingService.PAGE_SIZE_DEFAULT_VALUE;
	
	public PagedList(List<T> resultsList, Integer total, Integer offset) {
		super(resultsList);
		this.offset = offset;
		this.total = total;
		
	}
	
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}

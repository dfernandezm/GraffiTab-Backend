package com.graffitab.server.persistence.model;

import com.graffitab.server.persistence.dao.Identifiable;

public class Avatar implements Identifiable<Long> {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String image;
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}

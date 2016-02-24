package com.graffitab.server.persistence.model;

import com.graffitab.server.persistence.dao.Identifiable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode
public class Device implements Identifiable<Long> {

	private static final long serialVersionUID = 1L;

	public enum OSType {
		IOS,
		ANDROID;
	}

	private Long id;
	private String token;
	private OSType osType;
	private User user;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}

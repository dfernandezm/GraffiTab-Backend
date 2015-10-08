package com.graffitab.server.persistence.model;

import com.graffitab.server.persistence.dao.Identifiable;

/**
 * Created by david on 14/10/14.
 */
public class Person implements Identifiable<Long> {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	public String externalId;
	public String username;
	public String firstname;
	public String lastname;
	public String password;
	public String email;
	public String website;
	public String about;
	
    public Person() {

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

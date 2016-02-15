package com.graffitab.server.persistence.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.graffitab.server.persistence.dao.Identifiable;

/**
 * Created by david.
 */
@Getter @Setter
public class User implements Identifiable<Long>, UserDetails {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String guid;
	private String externalId;
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private String website;
	private String about;
	private Set<User> followers = new HashSet<>();
	private Set<User> following = new HashSet<>();
	private List<Asset> assets = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		GrantedAuthority g = new SimpleGrantedAuthority("ROLE_USER");

		return Collections.singletonList(g);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void follow(User userToFollow) {
		following.add(userToFollow);
	}

	public void unfollow(User userToUnfollow) {
		following.remove(userToUnfollow);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

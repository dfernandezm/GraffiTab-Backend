package com.graffitab.server.persistence.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.graffitab.server.persistence.dao.Identifiable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by david.
 */
@Getter @Setter
public class User implements Identifiable<Long>, UserDetails {

	public enum AccountStatus {
		PENDING_ACTIVATION, ACTIVE, SUSPENDED, RESET_PASSWORD;
	}

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
	private List<User> followers = new ArrayList<>();
	private List<User> following = new ArrayList<>();
	private List<Asset> assets = new ArrayList<>();
	private List<Device> devices = new ArrayList<>();
	private Boolean followedByCurrentUser = Boolean.FALSE;
	private Map<String, String> metadataItems = new HashMap<>();
	private AccountStatus accountStatus = AccountStatus.PENDING_ACTIVATION;

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

	/**
	 * @param followee
	 * @return true if the current user is following the specified user
	 */
	public boolean isFollowing(User followee) {
		return following.contains(followee);
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

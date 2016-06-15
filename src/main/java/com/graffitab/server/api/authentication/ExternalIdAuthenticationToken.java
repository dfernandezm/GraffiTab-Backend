package com.graffitab.server.api.authentication;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.graffitab.server.persistence.model.externalprovider.ExternalProviderType;

import lombok.Data;

@Data
public class ExternalIdAuthenticationToken implements Authentication {

	private static final long serialVersionUID = 1L;

	private String externalId;
	private String accessToken;
	private ExternalProviderType externalProviderType;
	private UserDetails user;
	private boolean authenticated = false;

	@Override
	public String getName() {
		return user.getUsername();
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getAuthorities();
	}
	@Override
	public Object getCredentials() {
		return accessToken;
	}
	@Override
	public Object getDetails() {
		return externalProviderType;
	}
	@Override
	public Object getPrincipal() {
		return user;
	}
	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}
	@Override
	public void setAuthenticated(boolean isAuthenticated)
			throws IllegalArgumentException {
		this.authenticated = isAuthenticated;
	}
}

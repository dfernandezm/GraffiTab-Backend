package com.graffitab.server.service;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.service.user.UserService;

public class GraffiTabUserDetailsService implements UserDetailsService {

	@Resource
	private UserService userService;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userService.findByUsernameOrEmail(username);
	}
}

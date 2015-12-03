package com.graffitab.server.config.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


//@EnableWebSecurity
public class GraffitabSecurityConfig {
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user").password("password").roles("USER").and()
			.withUser("admin").password("password").roles("USER", "ADMIN");
	}
	
	/**
	 * Configuration for REST API
	 * 
	 * @author davidfernandez
	 */
	//@Configuration
	//@Order(2)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		
		protected void configure(HttpSecurity http) throws Exception {
			
			http.antMatcher("/api/**")                               
				.authorizeRequests()
				    .antMatchers("/api/login", "/api/status").permitAll()  
					.anyRequest().hasRole("USER")
					.and()
				.httpBasic();
			
		}
	}
	

//	/**
//	 * Configuration for Form Login authentication
//	 * 
//	 * @author davidfernandez
//	 */
//	@Configuration
//	@Order(3)
//	public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
//
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			
//			http.authorizeRequests()
//			    .antMatchers("/login").permitAll()
//				.anyRequest().authenticated()
//				.and()
//				.formLogin();
//		}
//	}                             
			
	

}

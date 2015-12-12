package com.graffitab.server.config.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class GraffitabSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user").password("password").roles("USER").and()
			.withUser("admin").password("password").roles("USER", "ADMIN");
//		auth
//        .userDetailsService( loginService )
//        .passwordEncoder( new ShaPasswordEncoder() );
	}
	
	@Configuration
    @Order(1)
    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter{
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .antMatcher("/api/**")
                    .authorizeRequests()
                        .anyRequest().hasAnyRole("ADMIN", "USER")
                        .and()
                    .httpBasic();
        }
    }

	@Configuration
    @Order(2)
    public static class FormWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/client/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable() // HTTP with Disable CSRF
                    .authorizeRequests() // Authorize Request Configuration
                        .antMatchers("/status","/").permitAll()
                        .anyRequest().authenticated()
                        .and() // Login Form configuration for all others
                    .formLogin()
                        .loginPage("/login").permitAll()
                        .and() // Logout Form configuration
                    .logout().permitAll();
        }
	}
}

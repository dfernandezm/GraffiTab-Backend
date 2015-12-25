package com.graffitab.server.config.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.graffitab.server.api.authentication.JsonLoginAuthenticationFilter;
import com.graffitab.server.api.authentication.JsonLoginFailureHandler;
import com.graffitab.server.api.authentication.JsonLoginSuccessHandler;

@Configuration
@Order(4) // one more than the latest block
public class SecurityBeansConfig extends WebSecurityConfigurerAdapter {
	

	@Bean
    public JsonLoginAuthenticationFilter jsonAuthenticationFilter() throws Exception {
        JsonLoginAuthenticationFilter authFilter = new JsonLoginAuthenticationFilter();
        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST"));
        authFilter.setAuthenticationManager(authenticationManager());
        // Custom success handler - send 200 OK
        authFilter.setAuthenticationSuccessHandler(new JsonLoginSuccessHandler());
        // Custom failure handler - send 401 unauthorized
        authFilter.setAuthenticationFailureHandler(new JsonLoginFailureHandler());
        authFilter.setUsernameParameter("username");
        authFilter.setPasswordParameter("password");
        return authFilter;
    }
	
}

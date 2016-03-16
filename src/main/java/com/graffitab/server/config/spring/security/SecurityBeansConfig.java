package com.graffitab.server.config.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.graffitab.server.api.authentication.CommonAuthenticationEntryPoint;
import com.graffitab.server.api.authentication.ExternalProviderAuthenticationFilter;
import com.graffitab.server.api.authentication.SessionInvalidationFilter;
import com.graffitab.server.api.authentication.JsonLoginAuthenticationFilter;
import com.graffitab.server.api.authentication.JsonLoginFailureHandler;
import com.graffitab.server.api.authentication.JsonLoginSuccessHandler;

@Configuration
@Order(5) // one more than the latest block in GraffitabSecurityConfig
public class SecurityBeansConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public JsonLoginSuccessHandler jsonLoginSuccessHandler() {
		return new JsonLoginSuccessHandler();
	}

	@Bean
    public JsonLoginAuthenticationFilter jsonAuthenticationFilter() throws Exception {
        JsonLoginAuthenticationFilter authFilter = new JsonLoginAuthenticationFilter();
        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST"));
        authFilter.setAuthenticationManager(authenticationManager());

        // Custom success handler - send 200 OK
        authFilter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler());

        // Custom failure handler - send 401 unauthorized
        authFilter.setAuthenticationFailureHandler(new JsonLoginFailureHandler());
        authFilter.setUsernameParameter("username");
        authFilter.setPasswordParameter("password");
        return authFilter;
    }

	@Bean
	public AuthenticationEntryPoint commonAuthenticationEntryPoint() {
		AuthenticationEntryPoint commonEntryPoint = new CommonAuthenticationEntryPoint();
		return commonEntryPoint;
	}

	@Bean
	public SessionInvalidationFilter invalidateSessionFilter() {
		return new SessionInvalidationFilter(commonAuthenticationEntryPoint());
	}

	@Bean
	public ExternalProviderAuthenticationFilter externalProviderAuthenticationFilter() throws Exception {
		ExternalProviderAuthenticationFilter externalProviderFilter =
												new ExternalProviderAuthenticationFilter();
		externalProviderFilter.setAuthenticationManager(authenticationManager());
		externalProviderFilter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler());
		externalProviderFilter.setAuthenticationFailureHandler(new JsonLoginFailureHandler());
		return externalProviderFilter;
	}

}

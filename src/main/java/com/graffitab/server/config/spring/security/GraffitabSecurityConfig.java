package com.graffitab.server.config.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.graffitab.server.api.authentication.CommonAuthenticationEntryPoint;
import com.graffitab.server.api.authentication.CustomFailureBasicAuthFilter;
import com.graffitab.server.api.authentication.JsonAccessDeniedHandler;
import com.graffitab.server.api.authentication.JsonLoginAuthenticationFilter;
import com.graffitab.server.api.authentication.JsonLoginFailureHandler;
import com.graffitab.server.api.authentication.JsonLoginSuccessHandler;
import com.graffitab.server.api.authentication.OkResponseLogoutHandler;


@Configuration
@EnableWebSecurity
public class GraffitabSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user").password("password").roles("USER").and()
			.withUser("admin").password("password").roles("USER", "ADMIN");		
	}

	@Configuration
    @Order(1)
    public static class LoginEndpointWebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .anonymous().disable()
                    .antMatcher("/api/login")
                    .authorizeRequests()
                       .antMatchers(HttpMethod.POST)
                       .permitAll()
                    .and()
            	    .sessionManagement()
            	    	.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
            
            JsonLoginAuthenticationFilter filter = customAuthenticationFilter();
            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        }
        
        @Bean
        public JsonLoginAuthenticationFilter customAuthenticationFilter() throws Exception {
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
        
	@Configuration
    @Order(2)
    public static class SessionAndBasicAuthSecurityConfig extends WebSecurityConfigurerAdapter{
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .anonymous().disable()
                    .antMatcher("/api/**")
            	    .sessionManagement()
            	    	.sessionCreationPolicy(SessionCreationPolicy.NEVER)
            	    .and()
                    .authorizeRequests()
                        .anyRequest().hasAnyRole("ADMIN", "USER")
                    .and()
                    .logout()
                       .deleteCookies("JSESSIONID").invalidateHttpSession(true)
 				       .logoutUrl("/api/logout").logoutSuccessHandler(new OkResponseLogoutHandler());

            // Add the custom filter before the regular one
            http.addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
            
            // Add the basic auth filter before the jsonLogin filter (check first)
            http.addFilterBefore(new CustomFailureBasicAuthFilter(authenticationManager()), 
            		    JsonLoginAuthenticationFilter.class);
            
            // Common entry points: 401 Unauthorized and access denied handlers
            http.exceptionHandling().authenticationEntryPoint(new CommonAuthenticationEntryPoint());
            http.exceptionHandling().accessDeniedHandler(new JsonAccessDeniedHandler());
        }
        
        @Bean
        public JsonLoginAuthenticationFilter customAuthenticationFilter() throws Exception {
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
}

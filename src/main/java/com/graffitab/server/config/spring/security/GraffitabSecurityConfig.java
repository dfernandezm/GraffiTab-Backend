package com.graffitab.server.config.spring.security;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.graffitab.server.api.authentication.CommonAuthenticationEntryPoint;
import com.graffitab.server.api.authentication.JsonAccessDeniedHandler;
import com.graffitab.server.api.authentication.JsonLoginAuthenticationFilter;
import com.graffitab.server.api.authentication.JsonLoginFailureHandler;
import com.graffitab.server.api.authentication.JsonLoginSuccessHandler;


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
    public static class LoginEndpointWebSecurityConfig extends WebSecurityConfigurerAdapter{
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
            authFilter.setAuthenticationSuccessHandler(new JsonLoginSuccessHandler());
            authFilter.setAuthenticationFailureHandler(new JsonLoginFailureHandler());
            authFilter.setUsernameParameter("username");
            authFilter.setPasswordParameter("password");
            return authFilter;
        }
        
	}
        
        

	
	@Configuration
    @Order(2)
    public static class BasicAuthWebSecurityConfig extends WebSecurityConfigurerAdapter{
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .anonymous().disable()
                    .antMatcher("/api/**")
            	    .sessionManagement()
            	    	.sessionCreationPolicy(SessionCreationPolicy.NEVER)
            	    .and()
                    .authorizeRequests()
                        .anyRequest().hasAnyRole("ADMIN", "USER");
            //httpBasic()
            JsonLoginAuthenticationFilter filter = customAuthenticationFilter();
            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
            http.addFilterBefore(new BasicAuthenticationFilter(authenticationManager()), 
            		    JsonLoginAuthenticationFilter.class);
            http.exceptionHandling().authenticationEntryPoint(new CommonAuthenticationEntryPoint());
            http.exceptionHandling().accessDeniedHandler(new JsonAccessDeniedHandler());
        }
        
        @Bean
        public JsonLoginAuthenticationFilter customAuthenticationFilter() throws Exception {
            JsonLoginAuthenticationFilter authFilter = new JsonLoginAuthenticationFilter();
            authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST"));
            authFilter.setAuthenticationManager(authenticationManager());
            authFilter.setAuthenticationSuccessHandler(new JsonLoginSuccessHandler());
            authFilter.setAuthenticationFailureHandler(new JsonLoginFailureHandler());
            authFilter.setUsernameParameter("username");
            authFilter.setPasswordParameter("password");
            return authFilter;
        }
        
        
    }

	/**
	 * 
	 * curl -i -H "Content-Type: application/json" -X POST \ 
	 * -d '{"username":"user","password":"password"}' http://localhost:8090/api/login
	 * 
	 * curl -i -H "Content-Type: application/json" \ 
	 * --cookie "JSESSIONID=FAD70D53B04C8B6EF6E72D9141EA7C4D" http://localhost:8090/api/users
	 * 
	 * @author david
	 *
	 */
	@Configuration
    @Order(3)
    public static class SessionAuthWebSecurityConfig extends WebSecurityConfigurerAdapter{
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .antMatcher("/api/login")
                    .authorizeRequests()
                       .antMatchers(HttpMethod.POST)
                       .permitAll()
                     .and()
                     .antMatcher("/api/**")
                     .authorizeRequests()
                     .anyRequest().hasAnyRole("ADMIN", "USER");
            
            http.addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
            http.exceptionHandling().authenticationEntryPoint(new CommonAuthenticationEntryPoint());
            http.exceptionHandling().accessDeniedHandler(new JsonAccessDeniedHandler());   
        }
        
        
        @Bean
        public JsonLoginAuthenticationFilter customAuthenticationFilter() throws Exception {
            JsonLoginAuthenticationFilter authFilter = new JsonLoginAuthenticationFilter();
            authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST"));
            authFilter.setAuthenticationManager(authenticationManager());
            authFilter.setAuthenticationSuccessHandler(new JsonLoginSuccessHandler());
            authFilter.setAuthenticationFailureHandler(new JsonLoginFailureHandler());
            authFilter.setUsernameParameter("username");
            authFilter.setPasswordParameter("password");
            return authFilter;
        }
    }
	
	private static class BasicAuthRequestedMatcher implements RequestMatcher {

		@Override
		public boolean matches(HttpServletRequest request) {
			
			String authHeader = request.getHeader("Authorization");
			
			if (authHeader != null && authHeader.contains("Basic")) {
				return true;
			}
			
			return false;
		}
	}
}

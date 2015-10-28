package com.graffitab.server.test.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graffitab.server.api.user.UserApiController;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.UserService;

@ContextConfiguration({"classpath:spring-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Rollback(value = true)
@WebAppConfiguration
public class UserApiTest {
	
	    @Resource
	    private WebApplicationContext ctx;
	    
	    @Resource
	    private UserService userService;
	    
	    private static User testUser;
	 
	    private MockMvc mockMvc;
	 
	    @Before
	    public void setUp() {
	        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
	    }
	    
	    @After
	    public void clear() {
	    	// Nothing to do
	    }
	 
	    @Test
	    @Transactional
	    public void getUserByIdTest() throws Exception {
	    	createUser();
	        mockMvc.perform(get("/api/users/{id}",testUser.getId()).accept(MediaType.APPLICATION_JSON))
	                .andExpect(status().isOk())
	                .andExpect(content().contentType("application/json;charset=UTF-8"))
	                .andExpect(jsonPath("$.user.id").value(testUser.getId().intValue()));
	        
	    }
	    
	    @Test
	    @Transactional
	    public void createUserTest() throws Exception {
	    	fillTestUser();
	    	InputStream in = this.getClass().getResourceAsStream("/api/user.json");
	    	String json = IOUtils.toString(in);
	    
	    	mockMvc.perform(post("/api/users/register")
	                .contentType("application/json;charset=UTF-8")
	                .content(json))
	                .andExpect(status().is(201))
	                .andExpect(content().contentType("application/json;charset=UTF-8"))
	                .andExpect(jsonPath("$.user.id").isNotEmpty())
	                .andExpect(jsonPath("$.user.username").isNotEmpty())
    				.andExpect(jsonPath("$.user.email").isNotEmpty());		
	    }
	    
	    private User fillTestUser() {
	    	testUser = new User();
	    	testUser.setFirstName("a");
	    	testUser.setLastName("b");
	    	testUser.setEmail("a@a.com");
	    	testUser.setUsername("ab");
	    	testUser.setPassword("pass");
	    	return testUser;
	    }
	    
	    private User createUser() {
	    	fillTestUser();
	    	userService.persist(testUser);
	    	return testUser;
	    }
	 
	    
	    
	 
	    
	    @Configuration
	    @ImportResource("classpath:graffitab-servlet-test.xml")
	    public static class TestConfiguration {
	 
	        @Bean
	        public UserApiController userApiController() {
	            return new UserApiController();
	        }
	 
	    }
	
}
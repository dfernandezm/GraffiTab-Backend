package com.graffitab.server.test.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.graffitab.server.api.user.UserApiController;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.UserService;

@ContextConfiguration({"classpath:spring-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
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
	        createUser();
	    }
	    
	    @After
	    public void clear() {
	    	deleteUser();
	    }
	 
	    @Test
	    public void getUserByIdTest() throws Exception {
	        mockMvc.perform(get("/api/users/{id}",testUser.getId()).accept(MediaType.APPLICATION_JSON))
	                .andExpect(status().isOk())
	                .andExpect(content().contentType("application/json;charset=UTF-8"))
	                .andExpect(jsonPath("$.user.id").value(testUser.getId().intValue()));
	    }
	    
	    private User createUser() {
	    	testUser = new User();
	    	testUser.setFirstName("a");
	    	testUser.setLastName("b");
	    	testUser.setEmail("a@a.com");
	    	testUser.setUsername("ab");
	    	testUser.setPassword("pass");
	    	userService.persist(testUser);
	    	return testUser;
	    }
	    
	    private void deleteUser() {
	    	if (testUser != null) {
	    		userService.remove(testUser.getId());
	    	}
	    }
	 
	    @Configuration
	    @EnableWebMvc
	    public static class TestConfiguration {
	 
	        @Bean
	        public UserApiController userApiController() {
	            return new UserApiController();
	        }
	 
	    }
	
}
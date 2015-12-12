package com.graffitab.server.test.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.graffitab.server.config.MainConfig;
import com.graffitab.server.config.web.WebConfig;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.UserService;

//@ContextConfiguration({"classpath:spring-context-test.xml"})

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={MainConfig.class, TestDatabaseConfig.class, WebConfig.class})
@Rollback(value = true)
@ActiveProfiles("unit-test")
public class UserApiTest {
	   
	    @Resource
	    private WebApplicationContext ctx;
	    
	    @Resource
	    private UserService userService;
	    
	    private static User testUser;
	    
	    private static User testUser2;
	 
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
	    
	    @Test
	    @Transactional
	    public void addFollowerToUserTest() {
	    	User user1 = fillTestUser();
	    	User userFollower = fillTestUser();
	    	
	    	userService.persist(user1);
	    	userService.persist(userFollower);
	    	
	    	user1.getFollowers().add(userFollower);
	    	//TODO: This gives null!!
//	    	userService.flush();
//	    	
//	    	userFollower = userService.findUserById(userFollower.getId());
//	    	
//	    	assertEquals(userFollower.getFollowing().size(), 1);
//	    	assertTrue(userFollower.getFollowing().contains(user1));
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
	    
	    private User fillTestUser2() {
	    	testUser2 = new User();
	    	testUser2.setFirstName("b");
	    	testUser2.setLastName("c");
	    	testUser2.setEmail("c@c.com");
	    	testUser2.setUsername("abc");
	    	testUser2.setPassword("pass2");
	    	return testUser2;
	    }
	    
	    private User createUser() {
	    	fillTestUser();
	    	userService.persist(testUser);
	    	return testUser;
	    }
	
}
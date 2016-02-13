package com.graffitab.server.test.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.graffitab.server.config.spring.MainConfig;
import com.graffitab.server.config.web.WebConfig;
import com.graffitab.server.persistence.model.AssetType;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.UserService;

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

	    @Autowired
	    private Filter springSecurityFilterChain;

	    private static User testUser;

	    private static User testUser2;

	    private MockMvc mockMvc;

	    @Before
	    public void setUp() {
	        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
	        		                      .addFilters(springSecurityFilterChain)
	        		                      .build();
	    }

	    @After
	    public void clear() {
	    	// Nothing to do
	    }

	    @Test
	    @Transactional
	    public void getUserByIdTest() throws Exception {
	    	User loggedInUser = createUser();
	    	createUser();
	        mockMvc.perform(get("/api/users/{id}",testUser.getId()).
	        		with(user(loggedInUser))
	        		.accept(MediaType.APPLICATION_JSON))
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

	   // @Test
	    @Transactional
	    public void addFollowerToUserTest() {
	    	User user1 = fillTestUser();
	    	User userFollower = fillTestUser();

	    	userService.saveUser(user1);
	    	userService.saveUser(userFollower);

	    	user1.getFollowers().add(userFollower);
	    	//TODO: This gives null!!
//	    	userService.flush();
//
//	    	userFollower = userService.findUserById(userFollower.getId());
//
//	    	assertEquals(userFollower.getFollowing().size(), 1);
//	    	assertTrue(userFollower.getFollowing().contains(user1));
	    }

	    @Test
	    @Transactional
	    public void addAssetTest() throws IOException, Exception {
	    	User loggedInUser = createUser();
	    	InputStream in = this.getClass().getResourceAsStream("/api/test-asset.jpg");
	    	mockMvc.perform(post("/api/users/avatar")
	    			//.header("Authorization", "Basic " + new String(Base64.encode(authorize.getBytes())))
	    			.with(user(loggedInUser))
	                .contentType("application/octet-stream")
	                .content(IOUtils.toByteArray(in)))
	                .andExpect(status().is(200))
	                .andExpect(content().contentType("application/json;charset=UTF-8"))
	                .andExpect(jsonPath("$.asset.guid").isNotEmpty())
	                .andExpect(jsonPath("$.asset.type").value(AssetType.AVATAR.name()));
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

	    @SuppressWarnings("unused")
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
	    	userService.saveUser(testUser);
	    	return testUser;
	    }

}
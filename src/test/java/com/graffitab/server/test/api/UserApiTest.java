package com.graffitab.server.test.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;

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

@ContextConfiguration({"classpath:spring-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class UserApiTest {
	
	    @Resource
	    private WebApplicationContext ctx;
	 
	    private MockMvc mockMvc;
	 
	    @Before
	    public void setUp() {
	        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
	    }
	 
	    @Test
	    public void getUserByIdTest() throws Exception {
	        Integer id = 1;
	        mockMvc.perform(get("/api/users/{id}",id).accept(MediaType.APPLICATION_JSON))
	                .andExpect(status().isOk())
	                .andExpect(content().contentType("application/json;charset=UTF-8"))
	                .andExpect(jsonPath("$.user.id").value(id));
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
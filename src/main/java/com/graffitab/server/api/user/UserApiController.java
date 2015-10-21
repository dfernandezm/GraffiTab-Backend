package com.graffitab.server.api.user;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.BaseApiController;
import com.graffitab.server.api.dto.user.CreateOrUpdateUserResult;
import com.graffitab.server.api.dto.user.DeleteUserResult;
import com.graffitab.server.api.dto.user.GetUserResult;
import com.graffitab.server.api.dto.user.ListUsersResult;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController extends BaseApiController {

	private static Logger LOG = LogManager.getLogger();
	
	@Resource
	private UserService userService;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public GetUserResult getUser(@PathVariable("id") Long id) {
		
		GetUserResult getUserResult = new GetUserResult();
		User user = userService.getUserById(id);
		
		LOG.info("Returning user with id " + id);
		
		getUserResult.setUser(user);
		return getUserResult;
	}
	
	
	@RequestMapping(value = {"","/{id}","/register"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	public CreateOrUpdateUserResult createUser(@JsonProperty("user") User user) {
		
		CreateOrUpdateUserResult createUserResult = new CreateOrUpdateUserResult();
		
		//TODO: Separate validation
		if (user.getFirstName() != null && user.getEmail() != null) {
			if (user.getId() == null) {
				userService.persist(user);
			}
		}
		
		LOG.info("Created user with ID " + user.getId());
		createUserResult.setUser(user);
		return createUserResult;
	}
	
	@RequestMapping(value = {""}, method = RequestMethod.GET, produces={"application/json"})
	public ListUsersResult listUsers() {
		ListUsersResult listUsersResult = new ListUsersResult();
		List<User> users = new ArrayList<>(); 
		listUsersResult.setUsers(users);
		
		//TODO: listUsers
		return listUsersResult;
	}
	
	@RequestMapping(value = {"/{id}"}, method = RequestMethod.DELETE, produces={"application/json"})
	public DeleteUserResult deleteUser(@PathVariable("id") Long userId) {
		DeleteUserResult deleteUserResult = new DeleteUserResult();
		
		//TODO: deleteUser
		return deleteUserResult;
	}
	
	
	private Boolean validateUser(User user) {
		//TODO
		return null;
	}
	
	private Boolean isUsernameTaken(String username) {
		//TODO:
		return null;
	}
	
	
	
}

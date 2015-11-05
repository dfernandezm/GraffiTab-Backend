package com.graffitab.server.api.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.BaseApiController;
import com.graffitab.server.api.dto.user.CreateUserResult;
import com.graffitab.server.api.dto.user.DeleteUserResult;
import com.graffitab.server.api.dto.user.GetUserResult;
import com.graffitab.server.api.dto.user.ListUsersResult;
import com.graffitab.server.api.dto.user.UpdateUserResult;
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
	
	@RequestMapping(value = {"","/register"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	public CreateUserResult createUser(@PathVariable Map<String, String> pathVariables, @JsonProperty("user") User user) {
		
		CreateUserResult createUserResult = new CreateUserResult();
		
		if (validateUser(user)){
			
			if (pathVariables.get("id") == null) {
				userService.persist(user);
			}
			
			LOG.info("Created user with ID " + user.getId());
			createUserResult.setUser(user);
			return createUserResult;
			
		} else {
			//TODO: send 400 error with a error message.
			return createUserResult;
		}
	}
	
	@RequestMapping(value = {"/{id}"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public UpdateUserResult updateUser(@PathVariable Map<String, String> pathVariables, @JsonProperty("user") User user) {
		
		UpdateUserResult updateUserResult = new UpdateUserResult();
		
		if (pathVariables.get("id") != null) {
			
			Long id = Long.parseLong(pathVariables.get("id"));
			user.setId(id);
			
			if (validateUser(user) ){
				
				
				
				userService.merge(user);
			}
			
	
		
		// Set the result
		LOG.info("Updated user with ID " + user.getId());
		updateUserResult.setUser(user);
		return updateUserResult;
	} else {
		//TODO: send 400 error with a error message.
		return updateUserResult;
	}

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
		boolean validationResult = false;
		//TODO
		if ( StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getEmail()) ||
			 StringUtils.isEmpty(user.getFirstName()) || StringUtils.isEmpty(user.getLastName()) ||
			 StringUtils.isEmpty(user.getPassword())) {
			validationResult = false;
			
		} else {
			
			if (isUsernameTaken(user.getUsername(),user.getId()) || isEmailTaken(user.getEmail(),user.getId()) ){
				validationResult=false;
			} else {
				validationResult = true;
			}
		}
		
		return validationResult;
	}
	
	private Boolean isUsernameTaken(String username, Long userId) {
		if ( userId != null){
			return  !userService.findUsersWithUsername(username, userId).isEmpty();
		} else {
			return  !userService.findByUsername(username).isEmpty();
		}
	}
	
	private Boolean isEmailTaken(String email, Long userId) {
		if ( userId != null){
			return  !userService.findUsersWithEmail(email, userId).isEmpty();
		} else {
			return  !userService.findByEmail(email).isEmpty();
		}
	}
	
	
	
}

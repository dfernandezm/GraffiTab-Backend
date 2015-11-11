package com.graffitab.server.api.user;

import java.util.ArrayList;
import java.util.List;

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
import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.api.errors.EntityNotFoundException;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.api.errors.ValidationErrorException;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController extends BaseApiController {

	private static Logger LOG = LogManager.getLogger();
	
	@Resource
	private UserService userService;
	
	@Resource
	private OrikaMapper mapper;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public GetUserResult getUser(@PathVariable("id") Long id) {
		
		GetUserResult getUserResult = new GetUserResult();
		User user = userService.findUserById(id);
		
		if (user != null) {		
			LOG.info("Returning user with id " + id);
			getUserResult.setUser(mapper.map(user, UserDto.class));
		} else {
			throw new EntityNotFoundException(ResultCode.NOT_FOUND, "Could not find user with id " + id);
		}
		
		return getUserResult;
	}
	
	@RequestMapping(value = {"","/register"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public CreateUserResult createUser(@JsonProperty("user") UserDto userDto) {
		
		CreateUserResult createUserResult = new CreateUserResult();
		
		if (validateUser(userDto)){
			
			if (userDto.getId() == null) {
				
				User user = mapper.map(userDto, User.class);		
				userService.persist(user);
				
				LOG.info("Created user with ID " + user.getId());
				createUserResult.setUser(mapper.map(user, UserDto.class));
				
			} else {
				
				throw new RestApiException(ResultCode.BAD_REQUEST, "ID has been provided to create endpoint -- This is not allowed");
			}
			
		} else {
			
			throw new ValidationErrorException("Validation error creating user");
		}
		
		return createUserResult;
	}
	
	@RequestMapping(value = {"/{id}"}, method = RequestMethod.POST, consumes={"application/json"})
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public UpdateUserResult updateUser(@PathVariable("id") Long id, @JsonProperty("user") UserDto userDto) {
		
		// 0. Validate UserDto
	    // 1. Find user by id -- returns User entity
		// 2. Map UserDto onto User entity -- that does all the updates
		// 3. Map back to return the updated entity
		
		UpdateUserResult updateUserResult = new UpdateUserResult();
		
		if (validateUser(userDto)) {
			
			User user = userService.findUserById(id);
			mapper.map(userDto, user);
			
			LOG.info("Updated user with ID " + user.getId());
			updateUserResult.setUser(mapper.map(user, UserDto.class));
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
	
	private Boolean validateUser(UserDto userDto) {
		
		boolean validationResult = false;
		
		if ( StringUtils.isEmpty(userDto.getUsername()) || StringUtils.isEmpty(userDto.getEmail()) ||
			 StringUtils.isEmpty(userDto.getFirstName()) || StringUtils.isEmpty(userDto.getLastName()) ||
			 StringUtils.isEmpty(userDto.getPassword())) {
			validationResult = false;
			
		} else {
			
			if (isUsernameTaken(userDto.getUsername(),userDto.getId()) || isEmailTaken(userDto.getEmail(),userDto.getId()) ){
				validationResult = false;
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

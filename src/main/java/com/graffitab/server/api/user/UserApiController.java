package com.graffitab.server.api.user;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.graffitab.server.api.BaseApiController;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController extends BaseApiController {

	private static Logger LOG = LogManager.getLogger();
	
	@Resource
	private UserService userService;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public User getUser(@PathVariable("id") Long id) {
		User user = userService.getUserById(id);
		
		LOG.info("Returning user with id " + id);
		
		if (user == null) {
			return new User();
		}
		
		return user;
	}
	
	//TODO: createUser, updateUser, deleteUser
	
	
	
}

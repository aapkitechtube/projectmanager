package com.projectmanager.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanager.api.common.Constants;
import com.projectmanager.api.entity.User;
import com.projectmanager.api.exception.ResourceNotFoundException;
import com.projectmanager.api.repository.UserRepository;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(maxAge = 3600)
public class UserController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/users")
	public List<User> getAllUsers() {
		return userRepository.findAllUsers();
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(
			@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
		
		LOGGER.info("getUserById(): userId:", userId);
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_404_MSG + userId));
		return ResponseEntity.ok().body(user);
	}
	
	@GetMapping("/users/{projectId}/{taskId}")
	public ResponseEntity<User> getUserByProjectAndTaskId(
			@PathVariable(value = "projectId") Long projectId,
			@PathVariable(value = "taskId") Long taskId) throws ResourceNotFoundException {
		List<User> users = userRepository.findUserByProjectIdAndTaskId(projectId, taskId);
		if (users == null || users.size() == 0)
			return ResponseEntity.ok().body(new User());
		else
			return ResponseEntity.ok().body(users.get(0));
	}
	
	@GetMapping("/userbyproject/{projectId}")
	public ResponseEntity<Object> getUserByProjectId(
			@PathVariable(value = "projectId") Long projectId) throws ResourceNotFoundException {
		List<?> user = userRepository.findUserByProjectId(projectId);
		if (user == null || user.size() == 0)
			return ResponseEntity.ok().body(new User());
		else
			return ResponseEntity.ok().body(user.get(0));
	}

	@PostMapping("/users")
	public User createUser(@Valid @RequestBody User user) {
		return userRepository.save(user);
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(
			@PathVariable(value = "id") Long userId,
			@Valid @RequestBody User userDetails) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
		        .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_404_MSG + userId));
		
		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setEmployeeId(userDetails.getEmployeeId());
		user.setProjectId(userDetails.getProjectId());
		user.setTaskId(userDetails.getTaskId());
		
		final User updatedUser = userRepository.save(user);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/users/{id}")
	@CrossOrigin(maxAge = 3600)
	public Map<String, Boolean> deleteUser(
			@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
		        .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_404_MSG + userId));

		userRepository.delete(user);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
}

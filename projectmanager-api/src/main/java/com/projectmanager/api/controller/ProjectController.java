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
import com.projectmanager.api.entity.Project;
import com.projectmanager.api.entity.User;
import com.projectmanager.api.exception.ResourceNotFoundException;
import com.projectmanager.api.repository.ProjectRepository;
import com.projectmanager.api.repository.TaskRepository;
import com.projectmanager.api.repository.UserRepository;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(maxAge = 3600)
public class ProjectController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/projects")
	public List<Project> getAllProjects() {
		return projectRepository.findAll();
	}

	@GetMapping("/projects/{id}")
	public ResponseEntity<Project> getProjectById(
			@PathVariable(value = "id") Long projectId) throws ResourceNotFoundException {
		LOGGER.info("getProjectById(): projectId:", projectId);
		Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException(Constants.PROJECT_404_MSG + projectId));
		return ResponseEntity.ok().body(project);
	}

	@PostMapping("/projects/{userId}")
	public Project createProject(@Valid @RequestBody Project project,
			@PathVariable("userId") long userId) throws ResourceNotFoundException {
		
		Project createProject = projectRepository.save(project);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_404_MSG + userId));
		User manager = new User();
		manager.setFirstName(user.getFirstName());
		manager.setLastName(user.getLastName());
		manager.setEmployeeId(user.getEmployeeId());
		manager.setProjectId(createProject.getId());
		manager.setTaskId(user.getTaskId());
		userRepository.save(manager);
		return createProject;
	}

	@PutMapping("/projects/{projectId}/{userId}")
	public ResponseEntity<Project> updateProject(
			@PathVariable(value = "projectId") Long projectId,
			@PathVariable(value = "userId") Long userId,
			@Valid @RequestBody Project projectDetails) throws ResourceNotFoundException {
		
		Project project = projectRepository.findById(projectId)
		        .orElseThrow(() -> new ResourceNotFoundException(Constants.PROJECT_404_MSG + projectId));
		
		project.setProject(projectDetails.getProject());
		project.setStartDate(projectDetails.getStartDate());
		project.setEndDate(projectDetails.getEndDate());
		project.setPriority(projectDetails.getPriority());
		final Project updatedProject = projectRepository.save(project);
		
		List<User> users = userRepository.findUserByProjectId(projectId);
		if (users != null && users.size() > 0 && userId != users.get(0).getId()) {
			User user = users.get(0);
			user.setProjectId(0);
			userRepository.save(user);
			
			User user2 = userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_404_MSG + userId));
			user2.setProjectId(project.getId());
			user2.setProjectId(updatedProject.getId());
			userRepository.save(user2);
		}
		
		return ResponseEntity.ok(updatedProject);
	}

	@DeleteMapping("/projects/{id}")
	public Map<String, Boolean> deleteProject(
			@PathVariable(value = "id") Long projectId) throws ResourceNotFoundException {
		Project project = projectRepository.findById(projectId)
		        .orElseThrow(() -> new ResourceNotFoundException(Constants.PROJECT_404_MSG + projectId));

		projectRepository.delete(project);
		taskRepository.deleteTasksByProjectId(projectId);
		userRepository.deleteUsersByProjectId(projectId);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
	
	@GetMapping("/projects/tasks")
	public List<Map<String, String>> getProjectAndTaskDetails () {
		return projectRepository.getProjectAndTaskDetails();
	}
}

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
import com.projectmanager.api.entity.Task;
import com.projectmanager.api.entity.User;
import com.projectmanager.api.exception.ResourceNotFoundException;
import com.projectmanager.api.repository.TaskRepository;
import com.projectmanager.api.repository.UserRepository;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(maxAge = 3600)
public class TaskController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/tasks")
	public List<Task> getAllTasks() {
		return taskRepository.findAll();
	}

	@GetMapping("/tasks/{id}")
	public ResponseEntity<Task> getTaskById(
			@PathVariable(value = "id") Long TaskId) throws ResourceNotFoundException {
		
		LOGGER.info("getTaskById(): TaskId:", TaskId);
		
		Task Task = taskRepository.findById(TaskId)
        .orElseThrow(() -> new ResourceNotFoundException(Constants.TASK_404_MSG + TaskId));
		return ResponseEntity.ok().body(Task);
	}
	
	@GetMapping("/tasksbyproject/{projectId}")
	public ResponseEntity<List<Map<String, String>>> getTaskByProjectId(
			@PathVariable(value = "projectId") Long projectId) throws ResourceNotFoundException {
				
		List<Map<String, String>> tasks= taskRepository.findAllTasksByProjectId(projectId);
		return ResponseEntity.ok().body(tasks);
	}

	@PostMapping("/tasks/{userId}")
	public Task createTask(@Valid @RequestBody Task task,
			@PathVariable(value = "userId") Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_404_MSG + userId));
		Task createdTask =  taskRepository.save(task);
		
		User newTaskForUser = new User ();
		newTaskForUser.setFirstName(user.getFirstName());
		newTaskForUser.setLastName(user.getLastName());
		newTaskForUser.setEmployeeId(user.getEmployeeId());
		newTaskForUser.setProjectId(createdTask.getProjectId());
		newTaskForUser.setTaskId(createdTask.getId());
		userRepository.save(newTaskForUser);
		
		return createdTask;
	}
	
	@PutMapping("/tasks/status/{id}")
	public ResponseEntity<Task> updateTaskStatus(
			@PathVariable(value = "id") Long TaskId) throws ResourceNotFoundException {
		Task task = taskRepository.findById(TaskId)
		        .orElseThrow(() -> new ResourceNotFoundException(Constants.TASK_404_MSG + TaskId));
		task.setStatus("complete");
		taskRepository.save(task);
		return ResponseEntity.ok(task);
	}

	@PutMapping("/tasks/{id}/{userId}")
	public ResponseEntity<Task> updateTask(
			@PathVariable(value = "id") Long taskId,
			@PathVariable(value = "userId") Long userId,
			@Valid @RequestBody Task taskDetails) throws ResourceNotFoundException {
		
		LOGGER.info("updateTask(): taskId:"+ taskId + " UserId:" + userId);
		
		Task task = taskRepository.findById(taskId)
		        .orElseThrow(() -> new ResourceNotFoundException(Constants.TASK_404_MSG + taskId));
				
		task.setTask(taskDetails.getTask());
		task.setStartDate(taskDetails.getStartDate());
		task.setEndDate(taskDetails.getEndDate());
		task.setPriority(taskDetails.getPriority());
		task.setParentId(taskDetails.getParentId());
		final Task updatedTask = taskRepository.save(task);
		User user = userRepository.findUserByProjectIdAndTaskId(
				taskDetails.getProjectId(), taskId).get(0);
		
		// Update existing user: if any
		if (user.getId() != userId) {
			userRepository.deleteById(user.getId());
			// Update user
			User updateUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_404_MSG + userId));
			
			// Assign the task to the user
			User newAssigneduser = new User ();
			newAssigneduser.setEmployeeId(updateUser.getEmployeeId());
			newAssigneduser.setFirstName(updateUser.getFirstName());
			newAssigneduser.setLastName(updateUser.getLastName());
			newAssigneduser.setProjectId(updatedTask.getProjectId());
			newAssigneduser.setTaskId(updatedTask.getId());
			userRepository.save(newAssigneduser);
		}
				
		return ResponseEntity.ok(updatedTask);
	}

	@DeleteMapping("/tasks/{id}")
	public Map<String, Boolean> deleteTask(
			@PathVariable(value = "id") Long TaskId) throws ResourceNotFoundException {
		Task Task = taskRepository.findById(TaskId)
		        .orElseThrow(() -> new ResourceNotFoundException(Constants.TASK_404_MSG + TaskId));

		taskRepository.delete(Task);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
}

package com.projectmanager.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanager.api.common.Constants;
import com.projectmanager.api.entity.ParentTask;
import com.projectmanager.api.exception.ResourceNotFoundException;
import com.projectmanager.api.repository.ParentTaskRepository;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(maxAge = 3600)
public class ParentTaskController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ParentTaskController.class);
	
	@Autowired
	private ParentTaskRepository parentTaskRepository;
	
	@GetMapping("/parenttasks")
	public List<ParentTask> getAllParentTasks() {
		return parentTaskRepository.findAll();
	}

	@GetMapping("/parenttasks/{id}")
	public ResponseEntity<ParentTask> getParentTaskById(
			@PathVariable(value = "id") Long parentTaskId) throws ResourceNotFoundException {
		LOGGER.info("getParentTaskById(): parentTaskId: ", parentTaskId);
		ParentTask parentTask = parentTaskRepository.findById(parentTaskId)
        .orElseThrow(() -> new ResourceNotFoundException(Constants.PARENT_TASK_404_MSG + parentTaskId));
		return ResponseEntity.ok().body(parentTask);
	}

	@PostMapping("/parenttasks")
	public ParentTask createParentTask(@Valid @RequestBody ParentTask parentTask) {
		return parentTaskRepository.save(parentTask);
	}
}



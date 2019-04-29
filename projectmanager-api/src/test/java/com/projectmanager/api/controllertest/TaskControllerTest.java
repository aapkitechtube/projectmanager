package com.projectmanager.api.controllertest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanager.api.entity.Task;
import com.projectmanager.api.entity.User;
import com.projectmanager.api.repository.TaskRepository;
import com.projectmanager.api.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private TestRestTemplate testRestTemplate;

	@MockBean
	private TaskRepository mockTaskRepository;

	@MockBean
	private UserRepository mockUserRepository;

	@Autowired
	ResourceLoader resourceLoader;

	Task[] taskData = null;
	ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void init() throws JsonParseException, JsonMappingException, IOException {
		/**
		 * Load Test data from JSON file
		 */
		taskData = objectMapper.readValue(resourceLoader.getResource("classpath:task_test_data.json").getInputStream(),
				Task[].class);

		when(mockTaskRepository.findById(1L)).thenReturn(Optional.of(taskData[0]));
	}

	@Test
	public void find_taskId_OK() throws JSONException, JsonProcessingException {

		String expected = objectMapper.writeValueAsString(taskData[0]);
		ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/tasks/1", String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

		JSONAssert.assertEquals(expected, response.getBody(), false);
		verify(mockTaskRepository, times(1)).findById(1L);

	}
	
	@Test
    public void find_taskIdNotFound_404() throws Exception {
    	
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/tasks/100", String.class);
        JSONObject jsonObject = new JSONObject(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found :: 100",jsonObject.getString("message"));
    }

	@Test
	public void find_all_tasks_OK() throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(taskData[0]);
		when(mockTaskRepository.findAll()).thenReturn(tasks);

		String expected = objectMapper.writeValueAsString(tasks);
		ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/tasks", String.class);
		JSONAssert.assertEquals(expected, response.getBody(), false);
	}

	@Test
	public void update_task_status_OK() throws Exception {
		
		Task updatedTask = taskData[0];
		when(mockTaskRepository.save(any(Task.class))).thenReturn(updatedTask);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updatedTask), headers);

		ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/tasks/status/1", HttpMethod.PUT, entity,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		JSONAssert.assertEquals(om.writeValueAsString(updatedTask), response.getBody(), false);

		verify(mockTaskRepository, times(1)).findById(1L);
		verify(mockTaskRepository, times(1)).save(any(Task.class));
	}

	@Test
	public void save_task_OK() throws Exception {

		Task newTask = taskData[0];
		when(mockTaskRepository.save(any(Task.class))).thenReturn(newTask);

		User user = new User();
		when(mockUserRepository.save(any(User.class))).thenReturn(user);
		when(mockUserRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

		String expected = om.writeValueAsString(newTask);

		ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/tasks/1", newTask, String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		JSONAssert.assertEquals(expected, response.getBody(), false);

		verify(mockTaskRepository, times(1)).save(any(Task.class));

	}

	@Test
	public void update_task_OK() throws Exception {

		Task updatedTask = taskData[0];
		when(mockTaskRepository.save(any(Task.class))).thenReturn(updatedTask);

		User user = new User();
		List<User> users = new ArrayList<User>();
		users.add(user);
		when(mockUserRepository.save(any(User.class))).thenReturn(user);
		when(mockUserRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
		when(mockUserRepository.findUserByProjectIdAndTaskId(any(Long.class), any(Long.class))).thenReturn(users);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updatedTask), headers);

		ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/tasks/1/1", HttpMethod.PUT, entity,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		JSONAssert.assertEquals(om.writeValueAsString(updatedTask), response.getBody(), false);

		verify(mockTaskRepository, times(1)).findById(1L);
		verify(mockTaskRepository, times(1)).save(any(Task.class));

	}

	@Test
	public void delete_task_OK() {

		Task task = taskData[0];
		when(mockTaskRepository.findById(1L)).thenReturn(Optional.of(task));

		doNothing().when(mockTaskRepository).delete(task);

		HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
		ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/tasks/1", HttpMethod.DELETE, entity,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(mockTaskRepository, times(1)).findById(1L);
		verify(mockTaskRepository, times(1)).delete(task);
	}
}

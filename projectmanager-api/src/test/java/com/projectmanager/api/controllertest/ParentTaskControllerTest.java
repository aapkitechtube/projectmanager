package com.projectmanager.api.controllertest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.Charset;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanager.api.entity.ParentTask;
import com.projectmanager.api.repository.ParentTaskRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ParentTaskControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private TestRestTemplate testRestTemplate;

	@MockBean
	private ParentTaskRepository mockParentTaskRepository;

	@Autowired
	ResourceLoader resourceLoader;

	ParentTask parentTaskData = null;
	ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void init() throws JsonParseException, JsonMappingException, IOException {
		/**
		 * Load the test data from JSON file
		 */
		parentTaskData = objectMapper.readValue(
				resourceLoader.getResource("classpath:parent_task_test_data.json").getInputStream(), ParentTask.class);
		when(mockParentTaskRepository.findById(1L)).thenReturn(Optional.of(parentTaskData));
		
		List<ParentTask> parentTasks = new ArrayList<ParentTask> ();
		parentTasks.add(parentTaskData);
		when(mockParentTaskRepository.findAll()).thenReturn(parentTasks);
	}

	@Test
	public void find_parentTaskId_OK() throws JSONException, IOException {

		String expected = StreamUtils.copyToString(new ClassPathResource("parent_task_test_data.json").getInputStream(),
				Charset.defaultCharset());
		System.out.println(expected);
		ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/parenttasks/1", String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

		JSONAssert.assertEquals(expected, response.getBody(), false);
		verify(mockParentTaskRepository, times(1)).findById(1L);
	}
	
	@Test
    public void find_parenttaskIdNotFound_404() throws Exception {
    	
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/parenttasks/100", String.class);
        JSONObject jsonObject = new JSONObject(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Parent Tasks not found :: 100",jsonObject.getString("message"));
    }

	@Test
	public void find_allTasks_OK() throws Exception {
		List<ParentTask> parentTasks = new ArrayList<ParentTask> ();
		parentTasks.add(parentTaskData);
		String expected = objectMapper.writeValueAsString(parentTasks);
		ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/parenttasks", String.class);
		JSONAssert.assertEquals(expected, response.getBody(), false);
	}

	@Test
	public void save_parentTask_OK() throws Exception {

		ParentTask newParentTask = parentTaskData;
		when(mockParentTaskRepository.save(any(ParentTask.class))).thenReturn(newParentTask);

		String expected = om.writeValueAsString(newParentTask);

		ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/parenttasks", newParentTask,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		JSONAssert.assertEquals(expected, response.getBody(), false);

		verify(mockParentTaskRepository, times(1)).save(any(ParentTask.class));

	}

	/*@Test
	public void update_parentTask_OK() throws Exception {

		ParentTask updatedParentTask = new ParentTask(parentTaskData.getParentTask());
		when(mockParentTaskRepository.save(any(ParentTask.class))).thenReturn(updatedParentTask);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updatedParentTask), headers);

		ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/parenttasks/1", HttpMethod.PUT, entity,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		JSONAssert.assertEquals(om.writeValueAsString(updatedParentTask), response.getBody(), false);

		verify(mockParentTaskRepository, times(1)).findById(1L);
		verify(mockParentTaskRepository, times(1)).save(any(ParentTask.class));

	}

	@Test
	public void delete_parentTask_OK() throws JsonParseException, JsonMappingException, IOException {

		ParentTask parentTask = new ParentTask(parentTaskData.getParentTask());
		when(mockParentTaskRepository.findById(1L)).thenReturn(Optional.of(parentTask));

		doNothing().when(mockParentTaskRepository).delete(parentTask);

		HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
		ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/parenttasks/1", HttpMethod.DELETE, entity,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(mockParentTaskRepository, times(1)).findById(1L);
		verify(mockParentTaskRepository, times(1)).delete(parentTask);
	}*/
}

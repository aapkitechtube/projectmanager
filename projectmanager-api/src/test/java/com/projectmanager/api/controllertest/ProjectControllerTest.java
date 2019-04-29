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
import java.util.Map;
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
import com.projectmanager.api.entity.Project;
import com.projectmanager.api.entity.User;
import com.projectmanager.api.repository.ProjectRepository;
import com.projectmanager.api.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProjectControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private TestRestTemplate testRestTemplate;

	@MockBean
	private ProjectRepository mockProjectRepository;

	@MockBean
	private UserRepository mockUserRepository;

	@Autowired
	ResourceLoader resourceLoader;

	Project[] projectData = null;
	Map <String, String> projTaskDetails = null;
	ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	@Before
	public void init() throws JsonParseException, JsonMappingException, IOException {
		/**
		 * Load Test data from JSON file
		 */
		projectData = objectMapper.readValue(resourceLoader.getResource("classpath:project_test_data.json").getInputStream(),
				Project[].class);
		
		projTaskDetails = objectMapper.readValue(resourceLoader.getResource("classpath:project_task_test_data.json").getInputStream(),
				Map.class);

		when(mockProjectRepository.findById(1L)).thenReturn(Optional.of(projectData[0]));
	}

	@Test
	public void find_getProjectById_OK() throws JSONException, JsonProcessingException {

		String expected = objectMapper.writeValueAsString(projectData[0]);
		ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/projects/1", String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

		JSONAssert.assertEquals(expected, response.getBody(), false);
		verify(mockProjectRepository, times(1)).findById(1L);
	}
	
	@Test
	public void find_getProjectById_404() throws JSONException, JsonProcessingException {

		ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/projects/100", String.class);
        JSONObject jsonObject = new JSONObject(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Project not found :: 100",jsonObject.getString("message"));
	}
	
	@Test
    public void find_allProjects_OK() throws Exception {
    	List<Project> projects = new ArrayList<Project> ();
    	projects.add(projectData[0]);
    	when(mockProjectRepository.findAll()).thenReturn(projects);
    	String expected = objectMapper.writeValueAsString(projects);
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/projects", String.class);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
	
	@Test
	public void save_project_OK() throws Exception {
		
		Project newProject = projectData[0];
		when(mockProjectRepository.save(any(Project.class))).thenReturn(newProject);

		User user = new User();
		when(mockUserRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
		when(mockUserRepository.save(any(User.class))).thenReturn(user);

		String expected = om.writeValueAsString(newProject);
		ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/projects/1", newProject,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		JSONAssert.assertEquals(expected, response.getBody(), false);

		verify(mockProjectRepository, times(1)).save(any(Project.class));
	}

	@Test
	public void update_Project_OK() throws Exception {

		Project updatedProject = projectData[0];
		when(mockProjectRepository.save(any(Project.class))).thenReturn(updatedProject);

		List<User> users = new ArrayList<User>();
		User user = new User();
		users.add(user);
		when(mockUserRepository.findUserByProjectId(any(Long.class))).thenReturn(users);
		when(mockUserRepository.save(any(User.class))).thenReturn(user);
		when(mockUserRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updatedProject), headers);

		ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/projects/1/1", HttpMethod.PUT, entity,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		JSONAssert.assertEquals(om.writeValueAsString(updatedProject), response.getBody(), false);
	}

	@Test
	public void delete_user_OK() {

		Project project = projectData[0];
		when(mockProjectRepository.findById(1L)).thenReturn(Optional.of(project));

		doNothing().when(mockProjectRepository).delete(project);

		HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
		ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/projects/1", HttpMethod.DELETE, entity,
				String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(mockProjectRepository, times(1)).findById(1L);
		verify(mockProjectRepository, times(1)).delete(project);
	}
}

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
import com.projectmanager.api.entity.User;
import com.projectmanager.api.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {    

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private UserRepository mockUserRepository;
    
    @Autowired
	ResourceLoader resourceLoader;
    
    User[] userData = null;
	ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() throws JsonParseException, JsonMappingException, IOException {
    	/**
    	 * Load Test data from JSON file
    	 */
    	userData = objectMapper.readValue(
				resourceLoader.getResource("classpath:user_test_data.json").getInputStream(), User[].class);
    	
    	 when(mockUserRepository.findById(1L)).thenReturn(Optional.of(userData[0]));
    	 
    	 List<User> users = new ArrayList<User> ();
    	 users.add(userData[1]);
         when(mockUserRepository.findAllUsers()).thenReturn(users);
         
         users = new ArrayList<User> ();
    	 users.add(userData[0]);
         when(mockUserRepository.findUserByProjectIdAndTaskId(1,1)).thenReturn(users);
         when(mockUserRepository.findUserByProjectId(1)).thenReturn(users);
         
         users = new ArrayList<User> ();
         when(mockUserRepository.findUserByProjectIdAndTaskId(0,0)).thenReturn(users);
         when(mockUserRepository.findUserByProjectId(0)).thenReturn(users);
    }

    @Test
    public void find_userId_OK() throws JSONException, JsonProcessingException {
    	
    	User user = new User (
    		userData[0].getId(),
    		userData[0].getFirstName(),
    		userData[0].getLastName(),
    		userData[0].getEmployeeId()
    	);
    	
    	when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
    	 
        String expected = objectMapper.writeValueAsString(user);

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/users/1", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);
        verify(mockUserRepository, times(1)).findById(1L);

    }
    
    @Test
    public void find_userIdNotFound_404() throws Exception {
    	
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/users/100", String.class);
        JSONObject jsonObject = new JSONObject(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found :: 100",jsonObject.getString("message"));
    }
    
    @Test
    public void find_allUsers() throws Exception {
    	List<User> users = new ArrayList<User> ();
    	users.add(userData[1]);
    	String expected = objectMapper.writeValueAsString(users);
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/users", String.class);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
    @Test
    public void find_getUserByProjectAndTaskId_OK() throws Exception {
    	String expected = objectMapper.writeValueAsString(userData[0]);
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/users/1/1", String.class);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
    @Test
    public void find_getUserByProjectAndTaskId_NOT_OK() throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper();
    	String expected = objectMapper.writeValueAsString(new User());
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/users/0/0", String.class);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
    @Test
    public void find_getUserByProjectId_OK() throws Exception {
    	String expected = objectMapper.writeValueAsString(userData[0]);
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/userbyproject/1", String.class);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
    @Test
    public void find_getUserByProjectId_NOT_OK() throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper();
    	String expected = objectMapper.writeValueAsString(new User());
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/userbyproject/0", String.class);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
    @Test
    public void save_user_OK() throws Exception {

        when(mockUserRepository.save(any(User.class))).thenReturn(userData[2]);

        String expected = om.writeValueAsString(userData[2]);

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/users", userData[2], String.class);
        	
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockUserRepository, times(1)).save(any(User.class));
    }

    @Test
    public void update_user_OK() throws Exception {

        when(mockUserRepository.save(any(User.class))).thenReturn(userData[2]);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(userData[2]), headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/users/1", HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(om.writeValueAsString(userData[2]), response.getBody(), false);

        verify(mockUserRepository, times(1)).findById(1L);
        verify(mockUserRepository, times(1)).save(any(User.class));

    }
    
    @Test
    public void delete_user_OK() {
    	
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(userData[0]));
        doNothing().when(mockUserRepository).delete(userData[0]);

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/users/1", HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockUserRepository, times(1)).findById(1L);
        verify(mockUserRepository, times(1)).delete(userData[0]);
    }
}

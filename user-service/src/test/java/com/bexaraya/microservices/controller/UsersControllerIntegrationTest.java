package com.bexaraya.microservices.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.bexaraya.microservices.config.NoSecurityConfig;
import com.bexaraya.microservices.model.User;
import com.bexaraya.microservices.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(NoSecurityConfig.class)
@ActiveProfiles("test")
public class UsersControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @BeforeEach
    void setup() {
    	User user = new User(1L, "bex", "bex@mycomp.com");
    	User user2 = new User(2L, "bex2", "bex2@mycomp.com");
		when(userService.createUser(any(User.class)))
    		.thenReturn(user);
		
		List<User> users = new ArrayList<>();
		users.add(0, user);
		users.add(1, user2);
		when(userService.findAllUsers())
		.thenReturn(users);
		
		when(userService.findUserById(1L))
			.thenReturn(Optional.of(user));
		when(userService.findUserById(longThat(id -> id != 1L)))
			.thenReturn(Optional.empty());
		
		when(userService.updateUser(any(User.class), eq(1L)))
			.thenReturn(Optional.of(user));
		when(userService.updateUser(any(User.class), longThat(id -> id != 1L)))
			.thenReturn(Optional.empty());
		
		when(userService.deleteUserById(1L))
			.thenReturn(true);
		when(userService.deleteUserById(longThat(id -> id != 1L)))
			.thenReturn(false);
    }
    
    @Test
    void createUser_shouldReturnTheCreatedUser() throws Exception {
    	String json = """
                {
                    "name": "bex",
                    "email": "bex@mycomp.com"
                }
                """;

            mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("bex"))
                .andExpect(jsonPath("$.email").value("bex@mycomp.com"))
                .andExpect(jsonPath("$.id").isNotEmpty()); // assigns auto id to the new user
    }
    
    @Test
    void findAllUser_shouldReturnAllUsers() throws Exception {
    	mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].name").value("bex2"));
    }
    
    @Test
    void findUserById_shouldReturnsTheSpecifiedUser() throws Exception{
    	mockMvc.perform(get("/api/v1/users/{id}", 1L))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.name").value("bex"));
    	
    	mockMvc.perform(get("/api/v1/users/{id}", 2L))
    		.andExpect(status().isNotFound());
    }
    
    @Test
    void updateUser_shouldReturnsTheUpdatedUser() throws Exception{
    	String json = """
                {
                	"id": 1,
                    "name": "bex",
                    "email": "bex@mycomp.com"
                }
                """;
    	
    	mockMvc.perform(put("/api/v1/users/{id}", 1L)
			    			.contentType(MediaType.APPLICATION_JSON)
			                .content(json))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.name").value("bex"));
    	
    	mockMvc.perform(put("/api/v1/users/{id}", 2L)
			    			.contentType(MediaType.APPLICATION_JSON)
			                .content(json))
    		  .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteUserById_shouldDeleteTheSpecifiedUser() throws Exception{
    	mockMvc.perform(delete("/api/v1/users/{id}", 1L))
    			.andExpect(status().isNoContent());
    	
    	mockMvc.perform(delete("/api/v1/users/{id}", 2L))
				.andExpect(status().isNotFound());
    }
}

package com.bexaraya.microservices.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bexaraya.microservices.dto.UserDto;
import com.bexaraya.microservices.repository.OrderRepository;
import com.bexaraya.microservices.service.UserClient;

import config.NoSecurityConfig;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import({NoSecurityConfig.class, OrderControllerIntegrationTest.MockConfig.class })
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
    private UserClient userClient;
	
    @BeforeEach
    void setup() {
        when(userClient.getUserById(2L))
            .thenReturn(new UserDto(2L, "Pepe", "pepe@example.com"));
    }
	
	@Test
	void createOrder_shouldReturnSavedOrder() throws Exception {
		String json = """
				{
				    "product": "queso",
				    "price": 2.23,
				    "userId": 2
				}
				""";
		
		mockMvc.perform(post("/api/v1/orders")
							.contentType(MediaType.APPLICATION_JSON)
							.content(json))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.product").value("queso"))
			   .andExpect(jsonPath("$.price").value(2.23))
			   .andExpect(jsonPath("$.userId").value(2));
		
	}
	
	// Clase interna que crea un mock manualmente
    @TestConfiguration
    static class MockConfig {
        @Bean
        UserClient userClient() {
            return Mockito.mock(UserClient.class);
        }
    }
}

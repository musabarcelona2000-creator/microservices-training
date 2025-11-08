package com.bexaraya.microservices.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.bexaraya.microservices.config.NoSecurityConfig;
import com.bexaraya.microservices.dto.UserDto;
import com.bexaraya.microservices.model.Order;
import com.bexaraya.microservices.repository.OrderRepository;
import com.bexaraya.microservices.service.OrderService;
import com.bexaraya.microservices.service.UserClient;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(NoSecurityConfig.class)
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;

    @MockBean
    private UserClient userClient;

    @BeforeEach
    void setup() {
        
        when(orderService.createOrder(any(Order.class)))
        	.thenReturn(new Order(1L, "queso", 2.23, 2L));
        
        when(orderService.updateOrder(any(Order.class), eq(1L)))
    		.thenReturn(Optional.of(new Order(1L, "queso curado", 2.5, 1L)));
        
        when(orderService.updateOrder(any(Order.class), Mockito.longThat(id -> id != 1L)))
			.thenReturn(Optional.empty());
    	
        when(userClient.getUserById(2L))
            .thenReturn(new UserDto(2L, "Pepe", "pepe@example.com"));

        List<Order> list = new ArrayList<>();
        list.add(0, new Order(0L, "patata", 1.2, 0L));
        list.add(1, new Order(1L, "tomate", 2.4, 1L));

        when(orderService.findAllOrders()).thenReturn(list);
        
        when(orderService.findOrderById(5L))
        	.thenReturn(Optional.of(new Order(5L, "pan", 1.5, 1L)));
        
        when(orderService.findOrderById(Mockito.longThat(id -> id != 5L)))
    		.thenReturn(Optional.empty());
        
        when(orderService.deleteOrderById(1L))
    		.thenReturn(true);
        
        when(orderService.deleteOrderById(Mockito.longThat(id -> id != 1L)))
		.thenReturn(false);

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

    @Test
    void findAllOrders_shouldReturnAllSavedOrders() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].product").value("patata"))
            .andExpect(jsonPath("$[1].price").value(2.4));
    }
    
    @Test
    void findById_shouldReturnsTheSpecifiedOrder() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{id}", 5L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5L))
            .andExpect(jsonPath("$.product").value("pan"))
            .andExpect(jsonPath("$.price").value(1.5));
        
        mockMvc.perform(get("/api/v1/orders/{id}", 4L))
        	.andExpect(status().isNotFound());
    }
    
    @Test
    void updateOrder_shouldReturnUpdatedOrder() throws Exception {
        
    	String json = """
                {
                	"id": 1,
                    "product": "queso curado",
                    "price": 2.5,
                    "userId": 1
                }
                """;
    	
    	mockMvc.perform(put("/api/v1/orders/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.product").value("queso curado"))
            .andExpect(jsonPath("$.price").value(2.5))
            .andExpect(jsonPath("$.userId").value(1));
    	
    	mockMvc.perform(put("/api/v1/orders/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteOrderById_shouldDeleteTheSpecifiedOrder() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/{id}", 1L))
            .andExpect(status().isNoContent());
        
        mockMvc.perform(delete("/api/v1/orders/{id}", 2L))
        	.andExpect(status().isNotFound());
    }
}

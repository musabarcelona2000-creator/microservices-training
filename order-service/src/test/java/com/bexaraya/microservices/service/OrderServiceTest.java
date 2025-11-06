package com.bexaraya.microservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bexaraya.microservices.dto.UserDto;
import com.bexaraya.microservices.model.Order;
import com.bexaraya.microservices.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private UserClient userClient;
	
	@InjectMocks
	private OrderService orderService;
	
	@Test
	void createOrder_shouldSaveOrder() {
		// Arrange
		Order order = new Order(null, "Galleta", 1.5, 2L);
		UserDto userDto = new UserDto(2L, "Bexi", "bexi@gmail.com");
		when(userClient.getUserById(2L))
			.thenReturn(userDto);
		when(orderRepository.save(any(Order.class)))
	    	.thenReturn(order);
		
		// Act
		Order result = orderService.createOrder(order);
		
		// Assert
		assertNotNull(result);
		assertEquals(result.getProduct(), "Galleta");
		assertEquals(result.getUserId(), 2L);
		verify(orderRepository).save(order);
	}
	
	@Test
	void findById_shouldReturnOrder() {
		// Arrange
		Order order = new Order(1L, "Tomate", 1.5, 1L);
		when(orderRepository.findById(1L))
			.thenReturn(Optional.of(order));
		when(orderRepository.findById(2L))
		.thenReturn(Optional.empty());
		// Act
		Optional<Order> found = orderService.findOrderById(1L);
		Optional<Order> notFound = orderService.findOrderById(2L);
		// Assert
		assertNotNull(found);
		assertEquals("Tomate", found.get().getProduct());
		assertEquals(true, notFound.isEmpty());
		verify(orderRepository, times(2)).findById(anyLong());
	}
}

package com.bexaraya.microservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
	void findAllOrders_shouldReturnAllSavedOrders() {
		// Arrange
		List<Order> list = new ArrayList<>();
		list.add(0, new Order(1L, "Tomate", 1.5, 1L));
		list.add(1, new Order(2L, "Patatas", 2.3, 2L));
		when(orderRepository.findAll())
			.thenReturn(list);

		// Act
		List<Order> allOrders = orderService.findAllOrders();
		
		// Assert
		assertNotNull(allOrders);
		assertEquals(allOrders.size(), 2);
		assertEquals(allOrders.get(1).getProduct(), "Patatas");
		verify(orderRepository).findAll();
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
	
	@Test
    void updateOrder_shouldReturnUpdatedOrder() {
		// Arrange
		Order order = new Order(1L, "Tomate", 1.5, 1L);
		when(orderRepository.findById(1L))
			.thenReturn(Optional.of(order));
		when(orderRepository.findById(Mockito.longThat(id -> id != 1L)))
	    	.thenReturn(Optional.empty());
		when(orderRepository.save(any(Order.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		
		// Act
		Optional<Order> updated = orderService.updateOrder(order, 1L);
		Optional<Order> notUpdated = orderService.updateOrder(order, 2L);
		
		// Assert
		assertNotNull(updated);
		assertNotNull(notUpdated);
		assertFalse(updated.isEmpty());
		assertTrue(notUpdated.isEmpty());
	}
	
    @Test
    void deleteOrderById_shouldDeleteTheSpecifiedOrder() {
    	// Arrange
    	when(orderRepository.existsById(1L))
    		.thenReturn(true);
    	when(orderRepository.existsById(Mockito.longThat(id -> id != 1L)))
			.thenReturn(false);
    	
    	// Act
    	boolean deleted = orderService.deleteOrderById(1L);
    	boolean notDeleted = orderService.deleteOrderById(2L);
    	
    	// Assert
    	assertTrue(deleted);
    	assertFalse(notDeleted);
    }
}

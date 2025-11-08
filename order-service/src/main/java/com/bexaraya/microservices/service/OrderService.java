package com.bexaraya.microservices.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.bexaraya.microservices.controller.OrdersController;
import com.bexaraya.microservices.dto.UserDto;
import com.bexaraya.microservices.event.UserEvent;
import com.bexaraya.microservices.model.Order;
import com.bexaraya.microservices.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final UserClient userClient;
	
	@KafkaListener(topics = {"users-topic"}, groupId = "order-group")
	public void consumeUserEvent(UserEvent event) {
	    log.info("Usuario creado: {}", event);
	}

	public Order createOrder(Order order) {
		UserDto user = userClient.getUserById(order.getUserId());
		 // Si el usuario no existe, el ErrorDecoder lanzará UserNotFoundException automáticamente.
        // No necesitas manejarlo aquí si tienes el @ControllerAdvice.
		return orderRepository.save(order);
	}

	public List<Order> findAllOrders() {
		return orderRepository.findAll();
	}

	public Optional<Order> findOrderById(Long id) {
		return orderRepository.findById(id);
	}

	public Optional<Order> updateOrder(Order order, Long id) {
		order.setId(id);
		return orderRepository.findById(id)
					.map(existing -> orderRepository.save(order));
	}

	public boolean deleteOrderById(Long id) {
		if (orderRepository.existsById(id)) {
			orderRepository.deleteById(id);	
			return true;
		}
		return false;
	}

}

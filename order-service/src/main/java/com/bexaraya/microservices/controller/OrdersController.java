package com.bexaraya.microservices.controller;

import java.util.List;
import java.util.Optional;

import org.apache.kafka.clients.consumer.internals.events.ResetPositionsEvent;
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bexaraya.microservices.OrderServiceApplication;
import com.bexaraya.microservices.model.Order;
import com.bexaraya.microservices.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController {

	private final OrderService orderService;
	
	@PostMapping
	public ResponseEntity<Order> createOrder(@RequestBody Order order) {
		return ResponseEntity.ok(orderService.createOrder(order));
	}
	
	@GetMapping
	public List<Order> findAllOrders() {
		return orderService.findAllOrders();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Order> findOrderById(@PathVariable Long id) {
		return orderService.findOrderById(id)
			   .map(ResponseEntity::ok)
			   .orElse(ResponseEntity.notFound().build());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Order> updateOrder(@RequestBody Order order,
			@PathVariable Long id) {
		return orderService.updateOrder(order, id)
		   .map(ResponseEntity::ok)
		   .orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}")
	//Puedes combinar roles y claims complejos (hasRole('ADMIN') and #jwt.claims.department=='IT')
    // se basa en el claim roles del JWT
	//@PreAuthorize("hasRole('ADMIN')")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
		boolean deleted = orderService.deleteOrderById(id);
		if (deleted) {
			return ResponseEntity.noContent().build();	
		}
		else {
			return ResponseEntity.notFound().build();			
		}
	}
}

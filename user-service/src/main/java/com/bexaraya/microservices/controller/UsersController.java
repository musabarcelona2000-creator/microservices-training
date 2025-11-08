package com.bexaraya.microservices.controller;

import java.util.List;

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

import com.bexaraya.microservices.model.User;
import com.bexaraya.microservices.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

	private final UserService userService;
	
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		return ResponseEntity.ok(userService.createUser(user));
	}
	
	@GetMapping
	public List<User> findAllUser() {
		return userService.findAllUsers();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User> findUserById(@PathVariable Long id) {
		return userService.findUserById(id)
			   .map(ResponseEntity::ok)
			   .orElse(ResponseEntity.notFound().build());
			   
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@RequestBody User user,
			@PathVariable Long id) {
		return userService.updateUser(user, id)
						  .map(ResponseEntity::ok)
						  .orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}")
	//Puedes combinar roles y claims complejos (hasRole('ADMIN') and #jwt.claims.department=='IT')
    // se basa en el claim roles del JWT
	//@PreAuthorize("hasRole('ADMIN')")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
		boolean deleted = userService.deleteUserById(id);
		if (deleted) {
			return ResponseEntity.noContent().build();
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
}

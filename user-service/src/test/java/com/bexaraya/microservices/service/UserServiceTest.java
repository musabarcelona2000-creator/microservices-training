package com.bexaraya.microservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.event.InvocationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.bexaraya.microservices.event.UserEvent;
import com.bexaraya.microservices.model.User;
import com.bexaraya.microservices.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private KafkaTemplate<String, UserEvent> kafkaTemplate;
	
	@InjectMocks
	private UserService userService;

	@Test
	void createUser_shouldSaveUser() {
		// Arrange
		User user = new User(1L, "bex", "bex@mycomp.com");
		when(userRepository.save(any(User.class)))
			.thenReturn(user);
		when(kafkaTemplate.send(any(String.class), any(UserEvent.class)))
			.thenReturn(null);
		
		// Act
		User created = userService.createUser(user);
		
		// Assert
		assertNotNull(created);
		assertEquals("bex", created.getName());
		
		// Verify
		verify(userRepository, times(1)).save(user);
		verify(kafkaTemplate, times(1))
			.send(eq("users-topic"), any(UserEvent.class));
	}
	
	@Test
	void findAllUsers_shouldReturnAllUsers() {
		// Arrange
		List<User> list = new ArrayList<>();
		list.add(0, new User(1L, "bex", "bex@mycomp.com"));
		list.add(1, new User(2L, "alex", "alex@mycomp.com"));
		when(userRepository.findAll())
			.thenReturn(list );
		
		// Act
		List<User> returnedUsers = userService.findAllUsers();
		
		// Assert
		assertNotNull(returnedUsers);
		assertEquals(2, returnedUsers.size());
		assertEquals("alex", returnedUsers.get(1).getName());
		
		// Verify
		verify(userRepository, times(1)).findAll();
	}
	
	@Test
	void findUserById_shouldReturnsTheSpecifiedUser() {
		// Arrange
		User user = new User(1L, "bex", "bex@mycomp.com");
		when(userRepository.findById(1L))
			.thenReturn(Optional.of(user));
		when(userRepository.findById(longThat(id -> id != 1L)))
			.thenReturn(Optional.empty());
		
		// Act
		Optional<User> found = userService.findUserById(1L);
		Optional<User> notFound = userService.findUserById(2L);
		
		// Assert
		assertNotNull(notFound);
		assertTrue(found.isPresent());
		assertFalse(notFound.isPresent());
		assertEquals("bex", found.get().getName());
		
		// Verify
		verify(userRepository, times(2))
			.findById(any(Long.class));
	}
	
	@Test
	void updateUser_shouldUpdateTheSpecifiedUser() {
		// Arrange
		User user = new User(1L, "bex", "bex@mycomp.com");
		when(userRepository.findById(eq(1L)))
			.thenReturn(Optional.of(user));
		when(userRepository.findById(longThat(id -> id != 1L)))
			.thenReturn(Optional.empty());
		when(userRepository.save(any(User.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		
		// Act
		User userForUpdate = new User(1L, "bex", "bex@mycomp.com");
		Optional<User> updatedUser = userService.updateUser(userForUpdate, 1L);
		// Para no afectar el primero, crear un nuevo objeto
		User userForFail = new User(1L, "bex", "bex@mycomp.com");
		Optional<User> notUpdateUser = userService.updateUser(userForFail, 2L);
		
		// Assert
		assertNotNull(notUpdateUser);
		assertTrue(notUpdateUser.isEmpty());
		assertEquals(1L, updatedUser.get().getId());
		
		// Verify
		verify(userRepository, times(1))
			.save(any(User.class));
	}
	
	@Test
	void deleteUserById_shouldDeleteTheSpecifiedUser() {
		// Arrange
		when(userRepository.existsById(1L))
			.thenReturn(true);
		when(userRepository.existsById(longThat(id -> id != 1L)))
			.thenReturn(false);
		
		// Act
		boolean resultDeleted = userService.deleteUserById(1L);
		boolean resultNotDeleted = userService.deleteUserById(2L);
		
		// Assert
		assertTrue(resultDeleted);
		assertFalse(resultNotDeleted);
		
		// Verify
		verify(userRepository, times(1))
			.deleteById(any(Long.class));
	}
}

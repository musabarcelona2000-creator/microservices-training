package com.bexaraya.microservices.service;

import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bexaraya.microservices.event.UserEvent;
import com.bexaraya.microservices.model.User;
import com.bexaraya.microservices.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final KafkaTemplate<String, UserEvent> kafkaTemplate;

	public User createUser(User user) {
		User saved = userRepository.save(user);
		kafkaTemplate.send("users-topic", new UserEvent(saved.getId().toString(), saved.getEmail()));
		return saved;
	}
	
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	public Optional<User> findUserById(Long id) {
		return userRepository.findById(id);
	}
	
	public User updateUser(User user, Long id) {
		user.setId(id);
		return userRepository.save(user);
	}

	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}

}

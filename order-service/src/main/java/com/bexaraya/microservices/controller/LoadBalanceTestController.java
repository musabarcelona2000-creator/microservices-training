package com.bexaraya.microservices.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bexaraya.microservices.dto.UserDto;
import com.bexaraya.microservices.service.UserClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/lb/test")
@RequiredArgsConstructor
@Slf4j
public class LoadBalanceTestController {
	private final UserClient userClient;
	
    @GetMapping("/users/{id}/loop")
    public void test(@PathVariable Long id) {
        for (int i = 0; i < 10; i++) {
            UserDto userById = userClient.getUserById(id);
            log.info("Respuesta #" + i + " -> " + userById);
        }
    }
}

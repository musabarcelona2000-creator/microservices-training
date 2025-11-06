package com.bexaraya.microservices.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bexaraya.microservices.config.FeignConfig;
import com.bexaraya.microservices.dto.UserDto;

@FeignClient(name = "user-service", url = "${user-service.url}")
@ConditionalOnProperty(value = "feign.enabled", havingValue = "true", matchIfMissing = true)
public interface UserClient {

	@GetMapping("/usersvc/api/v1/users/{id}")
    UserDto getUserById(@PathVariable Long id);

}

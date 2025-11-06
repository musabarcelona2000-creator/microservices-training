package com.bexaraya.microservices.config;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.bexaraya.microservices.exceptions.CustomFeignErrorDecoder;
import com.bexaraya.microservices.service.UserClient;


@Configuration
public class FeignConfig implements RequestInterceptor{
	
    //@Value("${user-service.url}")
    //private String userServiceUrl;
	
	@Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }
	
	@Override
    public void apply(RequestTemplate template) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            String token = jwtAuth.getToken().getTokenValue();
            template.header("Authorization", "Bearer " + token);
        }
    }
}

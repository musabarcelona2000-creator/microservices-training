package com.bexaraya.microservices.exceptions;

import org.springframework.stereotype.Component;

import feign.Response;
import feign.codec.ErrorDecoder;

/*
 * Cada vez que el servicio remoto responde con un código de error 
 * (400, 404, 500, etc.), este decoder se activa 
 * y lanza la excepción correspondiente.
 * */

@Component
public class CustomFeignErrorDecoder implements ErrorDecoder{

	@Override
	public Exception decode(String methodKey, Response response) {
		
		int status = response.status();
        return switch (status) {
            case 400 -> new InvalidRequestException("Invalid request for remote service");
            case 404 -> new UserNotFoundException("User not found in the remote service");
            case 500 -> new ExternalServiceException("Internal error in the remote service");
            default -> new ExternalServiceException("Unknown error in the remote service: " + status);
        };
	}

}

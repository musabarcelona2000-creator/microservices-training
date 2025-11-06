package com.bexaraya.microservices.event;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserEventDeserializer implements Deserializer<UserEvent>{

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public UserEvent deserialize(String topic, byte[] data) {
		if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, UserEvent.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing UserEvent", e);
        }
	}

}

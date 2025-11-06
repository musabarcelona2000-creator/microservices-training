package com.bexaraya.microservices.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserEvent {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;
}

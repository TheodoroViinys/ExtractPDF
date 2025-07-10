package com.estudosspring.projeto.configurations;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class configuration {

    @Bean
    public ObjectNode initNode(){
        return new ObjectNode(JsonNodeFactory.instance);
    }
}

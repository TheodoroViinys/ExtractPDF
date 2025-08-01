package com.estudosspring.projeto.exceptions;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class InvalidFormatException extends RuntimeException {


    private final ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

    public InvalidFormatException() {
        super("Formato inválido! Por favor, envie apenas arquivos (.PDF)");
    }

    public ObjectNode getBody() {
        node.put("status", HttpStatus.CONFLICT.value());
        node.put("description", "Processing failed: unsupported or unknown file format.");
        return node;
    }
}

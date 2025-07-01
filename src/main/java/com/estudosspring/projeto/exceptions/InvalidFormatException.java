package com.estudosspring.projeto.exceptions;

public class InvalidFormatException extends RuntimeException{
    public InvalidFormatException() {
        super("Formato inválido! Por favor, envie apenas arquivos (.PDF)");
    }
}

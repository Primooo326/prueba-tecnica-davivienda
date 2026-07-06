package com.example.gestionpolizas.exception;

public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}

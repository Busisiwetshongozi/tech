package com.example.Tech.exceptions;

public class ProductNotFoundException extends RuntimeException {

    // Constructor that accepts a message
    public ProductNotFoundException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


package com.example.educationforeveryone.exceptions;

public class DoNotMatchException extends RuntimeException {
    public DoNotMatchException(String errorMessage) {
        super(errorMessage);
    }
}

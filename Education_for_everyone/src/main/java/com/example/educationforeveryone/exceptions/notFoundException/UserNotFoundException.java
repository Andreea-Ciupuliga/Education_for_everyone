package com.example.educationforeveryone.exceptions.notFoundException;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String errorMessage) {
        super(errorMessage);

    }
}

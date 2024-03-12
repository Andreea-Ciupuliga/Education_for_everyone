package com.example.educationforeveryone.exceptions.alreadyExistException;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}


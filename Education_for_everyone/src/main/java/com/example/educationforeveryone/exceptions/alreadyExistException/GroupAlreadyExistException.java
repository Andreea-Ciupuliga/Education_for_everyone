package com.example.educationforeveryone.exceptions.alreadyExistException;

public class GroupAlreadyExistException extends RuntimeException {

    public GroupAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}

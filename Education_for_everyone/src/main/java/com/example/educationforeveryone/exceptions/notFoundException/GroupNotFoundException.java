package com.example.educationforeveryone.exceptions.notFoundException;

public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

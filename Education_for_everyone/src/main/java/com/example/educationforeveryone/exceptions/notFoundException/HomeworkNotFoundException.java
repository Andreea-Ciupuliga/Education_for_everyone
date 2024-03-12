package com.example.educationforeveryone.exceptions.notFoundException;

public class HomeworkNotFoundException extends RuntimeException {

    public HomeworkNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
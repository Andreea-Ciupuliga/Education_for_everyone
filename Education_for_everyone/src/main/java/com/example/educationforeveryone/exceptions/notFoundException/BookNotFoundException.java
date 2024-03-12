package com.example.educationforeveryone.exceptions.notFoundException;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

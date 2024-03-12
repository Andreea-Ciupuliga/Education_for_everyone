package com.example.educationforeveryone.exceptions.alreadyExistException;

public class BookAlreadyExistException extends RuntimeException {

    public BookAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}

package com.example.Education_for_everyone.exceptions;

public class BookAlreadyExistException extends Exception{

    public BookAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}

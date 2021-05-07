package com.example.Education_for_everyone.exceptions;

public class BookNotFoundException extends Exception{

    public BookNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

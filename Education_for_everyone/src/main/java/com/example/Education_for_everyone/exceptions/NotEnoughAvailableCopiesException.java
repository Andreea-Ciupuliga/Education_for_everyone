package com.example.Education_for_everyone.exceptions;

public class NotEnoughAvailableCopiesException extends Exception{
    public NotEnoughAvailableCopiesException(String errorMessage) {
        super(errorMessage);
    }
}

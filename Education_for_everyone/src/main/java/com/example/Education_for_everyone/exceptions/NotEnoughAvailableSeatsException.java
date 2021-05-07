package com.example.Education_for_everyone.exceptions;

public class NotEnoughAvailableSeatsException extends Exception{
    public NotEnoughAvailableSeatsException(String errorMessage) {
        super(errorMessage);
    }
}

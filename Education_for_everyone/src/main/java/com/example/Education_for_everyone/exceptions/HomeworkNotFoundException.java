package com.example.Education_for_everyone.exceptions;

public class HomeworkNotFoundException extends Exception{

    public HomeworkNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
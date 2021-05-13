package com.example.Education_for_everyone.exceptions;

public class HomeworkAndStudentDoNotMatchException extends Exception{
    public HomeworkAndStudentDoNotMatchException(String errorMessage)
    {
        super(errorMessage);
    }
}
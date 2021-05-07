package com.example.Education_for_everyone.exceptions;

public class BookAndStudentDoNotMatchException extends Exception{
    public BookAndStudentDoNotMatchException(String errorMessage)
    {
        super(errorMessage);
    }
}

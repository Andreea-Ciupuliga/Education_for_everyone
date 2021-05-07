package com.example.Education_for_everyone.exceptions;

public class GroupAndStudentDoNotMatchException extends Exception{
    public GroupAndStudentDoNotMatchException(String errorMessage)
    {
        super(errorMessage);
    }
}

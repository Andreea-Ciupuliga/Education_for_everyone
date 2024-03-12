package com.example.educationforeveryone.exceptions;

public class StudentsBorrowBooksException extends RuntimeException {
    public StudentsBorrowBooksException(String errorMessage) {
        super(errorMessage);
    }
}

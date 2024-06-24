package com.example.educationforeveryone.utils;

import org.springframework.http.HttpStatus;

public class SuccessDto {
    private final Integer statusCode = HttpStatus.OK.value();

    private final String statusMessage = HttpStatus.OK.getReasonPhrase();

    public Integer getStatusCode() {
        return statusCode;
    }

    public SuccessDto() {

    }
}

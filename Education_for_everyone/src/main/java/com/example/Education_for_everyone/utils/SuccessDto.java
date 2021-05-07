package com.example.Education_for_everyone.utils;

import org.springframework.http.HttpStatus;


//in cazul in care requestul se executa corect vreau sa trimit un mesaj spre partenerul cu care comunic a.i. sa stie ca requestul s-a executat corect
public class SuccessDto {


    //trimitem status de OK pt cand mesajul se executa cu succes
    private final Integer statusCode= HttpStatus.OK.value();

    private final String statusMessage=HttpStatus.OK.getReasonPhrase();

    public Integer getStatusCode() {
        return statusCode;
    }

    public SuccessDto()
    {

    }
}

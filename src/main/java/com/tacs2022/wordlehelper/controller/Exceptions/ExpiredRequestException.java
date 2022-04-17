package com.tacs2022.wordlehelper.controller.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class ExpiredRequestException extends RuntimeException {
    public ExpiredRequestException(){
        super();
    }
}

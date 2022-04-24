package com.tacs2022.wordlehelper.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExpiredRequestException extends RuntimeException {
    public ExpiredRequestException(){
        super();
    }
}

package com.tacs2022.wordlehelper.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUserException extends RuntimeException {
    public InvalidUserException(){
        super();
    }
}

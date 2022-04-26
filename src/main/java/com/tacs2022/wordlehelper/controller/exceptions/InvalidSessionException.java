package com.tacs2022.wordlehelper.controller.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException(){
        super("Invalid session");
    }
}

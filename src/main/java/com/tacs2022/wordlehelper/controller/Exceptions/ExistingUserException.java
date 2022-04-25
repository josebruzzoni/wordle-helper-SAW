package com.tacs2022.wordlehelper.controller.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExistingUserException extends RuntimeException{
    public ExistingUserException(){
        super("Username is already in use");
    }
}

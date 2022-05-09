package com.tacs2022.wordlehelper.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Why status conflict? See https://stackoverflow.com/questions/3825990/http-response-code-for-post-when-resource-already-exists
@ResponseStatus(HttpStatus.CONFLICT)
public class ExistingUserException extends RuntimeException{
    public ExistingUserException(){
        super("Username is already in use");
    }
}

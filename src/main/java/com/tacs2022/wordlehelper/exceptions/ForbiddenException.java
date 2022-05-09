package com.tacs2022.wordlehelper.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
@ResponseBody
public class ForbiddenException extends RuntimeException {
	public ForbiddenException(String message) {
        super(message);
    }
}

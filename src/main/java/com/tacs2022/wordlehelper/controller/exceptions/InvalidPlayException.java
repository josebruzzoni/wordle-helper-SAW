package com.tacs2022.wordlehelper.controller.exceptions;

import com.tacs2022.wordlehelper.service.HelperService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPlayException extends RuntimeException{
    public InvalidPlayException() {
        super("A Wordle play must be of at least " + HelperService.WORD_LENGTH + " characters combined");
    }

    public InvalidPlayException(String message) {
        super(message);
    }
}

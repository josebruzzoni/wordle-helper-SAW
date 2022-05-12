package com.tacs2022.wordlehelper.exceptions;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParticipantAlreadyAddedException extends RuntimeException{

    public ParticipantAlreadyAddedException(String message) {
        super(message);
    }
}

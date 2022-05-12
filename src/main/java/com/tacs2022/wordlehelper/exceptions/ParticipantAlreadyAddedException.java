package com.tacs2022.wordlehelper.exceptions;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParticipantAlreadyAddedException extends RuntimeException{

    public ParticipantAlreadyAddedException(User participant, boolean isUser, Tournament tournament) {
        super(
                (isUser? participant.getUsername() + " is": "You are ")+ "already a participant of "+tournament.getName()
        );
    }
}

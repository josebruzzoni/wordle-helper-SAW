package com.tacs2022.wordlehelper.dtos.tournaments;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewParticipantDto {
    @NotNull(message = "participantName is mandatory")
    private String participantName;
}

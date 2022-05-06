package com.tacs2022.wordlehelper.dtos;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

/**
 * DTO class used for json responses. Wraps the response object in a json assigning
 * the desired fieldName identifier. Used for returning primitive types or arrays
 * where the json representation of the object would not have any key for the value.
 */
@Data
@AllArgsConstructor
public class JsonResponseDto {
    @JsonIgnore
    private String fieldName;
    @JsonIgnore
    private Object data;

    @JsonAnyGetter
    public Map<String, Object> any() {
        return Collections.singletonMap(fieldName, data);
    }
}

/*
ESTADO DE LOS DTOs

Tournaments
NewTournamentDto ==> queda asi

NewParticipantDto ==> se puede reemplazar por JsonResponseDto??? PROBAR SI FUNCIONA A LA INVERSA

OutputScoreboardDto ==> habria que ver la parte de user, el resto se puede hacer con jsonproperty

OutputTournamentDto ==> queda asi

User
NewUserDto ==> queda asi


 */
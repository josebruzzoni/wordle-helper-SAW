package com.tacs2022.wordlehelper.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
public class AddParticipantDto {
    @Getter @Setter
    @NotBlank(message = "idParticipant is mandatory")
    private Long idParticipant;
}

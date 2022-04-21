package com.tacs2022.wordlehelper.dtos.user;

import com.tacs2022.wordlehelper.domain.Language;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class NewResultDto {
    @NotNull(message = "Score cannot be null")
    private Integer score;
    @NotNull(message = "Language cannot be null")
    private Language language;
    @NotNull(message = "Date cannot be null")
    private LocalDate date;
}


package com.tacs2022.wordlehelper.dtos.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.user.Result;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class NewResultDto {
    @NotNull(message = "failedAttempts is mandatory")
    private Integer attempts;
    @NotNull(message = "Language is mandatory")
    private Language language;

    @JsonIgnore
    private LocalDate date = LocalDate.now();

    public Result fromDto() {
        return new Result(attempts, language, date);
    }
}


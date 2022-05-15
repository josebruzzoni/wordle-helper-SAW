package com.tacs2022.wordlehelper.dtos.user;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.user.Result;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class NewResultDto {
    @NotNull(message = "failedAttempts cannot be null")
    private Integer failedAttempts;
    @NotNull(message = "Language cannot be null")
    private Language language;
    private LocalDate date = LocalDate.now();

    public Result fromDto() {
        return new Result(failedAttempts, language, date);
    }
}


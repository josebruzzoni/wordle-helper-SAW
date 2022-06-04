package com.tacs2022.wordlehelper.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tacs2022.wordlehelper.domain.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Result {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private Integer attempts;
    private Language language;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date = LocalDate.now();

    public Result(Integer attempts, Language language, LocalDate date) {
        this.attempts = attempts;
        this.language = language;
        this.date = date;
    }

    public boolean matches(Result result) {
        return getDate().atStartOfDay().equals(result.getDate().atStartOfDay())
            && getLanguage().equals(result.getLanguage())
        ;
    }
}



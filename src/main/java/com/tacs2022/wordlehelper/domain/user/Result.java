package com.tacs2022.wordlehelper.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tacs2022.wordlehelper.domain.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Result {
    @Id @GeneratedValue @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull(message = "Attempts is mandatory")
    private Integer attempts;
    @NotNull(message = "Language is mandatory")
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



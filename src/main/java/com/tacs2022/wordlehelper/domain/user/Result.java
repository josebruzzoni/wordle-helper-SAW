package com.tacs2022.wordlehelper.domain.user;

import com.tacs2022.wordlehelper.domain.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Result {
    @Id @GeneratedValue
    private Long id;
    private Integer attempts;
    private Language language;
    private LocalDate date;

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



package com.tacs2022.wordlehelper.domain.user;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.dtos.user.NewResultDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Result {
    @Id @GeneratedValue
    private Long id;
    private Integer failedAttempts;
    private Language language;
    private LocalDate date;

    public Result(Integer failedAttempts, Language language, LocalDate date) {
        this.failedAttempts = failedAttempts;
        this.language = language;
        this.date = date;
    }

    @Value("score")
    public int getScore() {
        int attemptsByDay = 5;
        return attemptsByDay - failedAttempts;
    }
}



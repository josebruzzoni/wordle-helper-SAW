package com.tacs2022.wordlehelper.domain.user;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.dtos.user.NewResultDto;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
public class Result {
    @Id @GeneratedValue
    private Long id;
    private Integer score;
    private Language language;
    private LocalDate date;

    public Result(NewResultDto resultDto){
        this.score = resultDto.getScore();
        this.language = resultDto.getLanguage();
        this.date = resultDto.getDate();
    }
}



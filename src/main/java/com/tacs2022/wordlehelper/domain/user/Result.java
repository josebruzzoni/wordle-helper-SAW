package com.tacs2022.wordlehelper.domain.user;

import com.tacs2022.wordlehelper.domain.Language;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
public class Result {
    @Id @GeneratedValue
    private Long id;

    private Integer score;
    private Language language;
    private LocalDate date;
}

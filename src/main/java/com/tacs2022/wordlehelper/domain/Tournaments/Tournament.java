package com.tacs2022.wordlehelper.domain.Tournaments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Language language;
    private Visibility visibility;
}

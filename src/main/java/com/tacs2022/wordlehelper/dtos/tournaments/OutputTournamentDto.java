package com.tacs2022.wordlehelper.dtos.tournaments;

import java.time.LocalDate;
import java.util.List;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.dtos.user.OutputUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutputTournamentDto {
	
	private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Visibility visibility;
    private List<Language> languages;
    private String owner;
    private List<String> participants;
}

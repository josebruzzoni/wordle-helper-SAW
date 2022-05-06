package com.tacs2022.wordlehelper.dtos.tournaments;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutputTournamentDto {
	
	private Long id;
	private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Visibility visibility;
    private List<Language> languages;
    private String owner;
    private List<String> participants;
	
    public OutputTournamentDto(Tournament tournament) {
    	this.id = tournament.getId();
    	this.name = tournament.getName();
    	this.startDate = tournament.getStartDate();
    	this.endDate = tournament.getEndDate();
    	this.visibility = tournament.getVisibility();
    	this.languages = tournament.getLanguages();
    	this.owner = tournament.getOwner().getUsername();
    	this.participants = tournament.getParticipants().stream()
    			.map(User::getUsername)
    			.collect(Collectors.toList());
	}
    
    
}

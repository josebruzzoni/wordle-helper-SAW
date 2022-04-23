package com.tacs2022.wordlehelper.domain.tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputTournamentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Visibility visibility;
    @ElementCollection
    private List<Language> languages = new ArrayList<>();
    
    @ManyToOne
    private User owner;
    @ManyToMany
    private List<User> participants = new ArrayList<>();

    public Tournament(NewTournamentDto newTournamentDto, User owner) {
        this.name = newTournamentDto.getName();
        this.startDate = newTournamentDto.getStartDate();
        this.endDate = newTournamentDto.getEndDate();
        this.languages.addAll(newTournamentDto.getLanguages());
        this.visibility = newTournamentDto.getVisibility();
        this.participants.add(owner);
        this.owner = owner;
    }

    public void addParticipant(User newParticipant) {
        this.participants.add(newParticipant);
    }
    
    public Boolean isPrivate() {
    	return visibility.equals(Visibility.PRIVATE);
    }

    public Leaderboard generateLeaderboard(){
        //TODO: generate leaderboard
        return new Leaderboard();
    }
    
    public TournamentStatus getStatus() {
    	TournamentStatus status;
    	
    	if(isNotStarted()) {
    		status = TournamentStatus.NOTSTARTED;
    	}else if(isFinished()) {
    		status = TournamentStatus.FINISHED;
    	}else {
    		status = TournamentStatus.STARTED;
    	}
    	
    	return status;
    }
    
    private Boolean isNotStarted() {
    	return LocalDate.now().isBefore(startDate);
    }
    
    private Boolean isFinished() {
    	return endDate.isBefore(LocalDate.now());
    }

    public boolean endedToDate(LocalDate date) {
        return !endDate.isAfter(date);
    }

    public boolean startedToDate(LocalDate date) {
        return !startDate.isAfter(date);
    }
    
    public OutputTournamentDto getResponse() {
    	List<String> participants = this.participants.stream()
    			.map( (User user) -> user.getUsername() )
    			.collect(Collectors.toList());
    	String username = this.owner.getUsername();
    	return new OutputTournamentDto(name, startDate, endDate, visibility, languages, username, participants);
    }
}

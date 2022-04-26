package com.tacs2022.wordlehelper.domain.tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Visibility visibility;
    @ElementCollection
    private List<Language> languages;
    
    @ManyToOne
    private User owner;
    @ManyToMany
    private List<User> participants;

    public Tournament(NewTournamentDto newTournamentDto, User owner) {
        this.name = newTournamentDto.getName();
        this.startDate = newTournamentDto.getStartDate();
        this.endDate = newTournamentDto.getEndDate();
        this.languages = new ArrayList<>();
        this.languages.addAll(newTournamentDto.getLanguages());
        this.visibility = newTournamentDto.getVisibility();
        this.participants = new ArrayList<>();
        this.participants.add(owner);
        this.owner = owner;
    }
    
    public Boolean isAParticipant(User newParticipant) {
    	return this.participants.contains(newParticipant);
    }

    public void addParticipant(User newParticipant) {
        this.participants.add(newParticipant);
    }
    
    public Boolean isPrivate() {
    	return visibility.equals(Visibility.PRIVATE);
    }
    
    public Boolean isOwner(User possibleOwner) {
    	return this.owner.equals(possibleOwner);
    }

    public List<Scoreboard> generateLeaderboardToDate(LocalDate date){
        return participants.stream()
                .map(u->new Scoreboard(u, this))
                .sorted(Comparator.comparing(s->s.getBadScoreToDate(date)))
                .collect(Collectors.toList());
    }

    public int daysPassedToDate(LocalDate date){
        long days = inProgressToDate(date)? startDate.datesUntil(date).count()
                : endedToDate(date)? 1+startDate.datesUntil(endDate).count()
                : 0;

        return (int) days;
    }

    public boolean considers(Result result){
        return this.supportsLanguage(result.getLanguage()) && inProgressToDate(result.getDate());
    }

    private boolean supportsLanguage(Language language) {
        return languages.contains(language);
    }

    public boolean inProgressToDate(LocalDate date){
        return startedToDate(date) && !endedToDate(date);
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
        return date.isAfter(endDate);
    }

    public boolean startedToDate(LocalDate date) {
        return !startDate.isAfter(date);
    }

}

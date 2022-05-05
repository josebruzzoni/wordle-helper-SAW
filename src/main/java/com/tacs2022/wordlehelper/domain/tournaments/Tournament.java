package com.tacs2022.wordlehelper.domain.tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
    
    public Boolean hasParticipant(User participant) {
    	return this.participants.contains(participant);
    }

    public void addParticipant(User newParticipant) {
        this.participants.add(newParticipant);
    }
    
    public Boolean userIsOwner(User user) {
    	return this.owner.equals(user);
    }

    public List<Scoreboard> generateLeaderboardAtDate(LocalDate date){
        return participants.stream()
                .map(u->new Scoreboard(u, this))
                .sorted(Comparator.comparing(s->s.getScoreAtDate(date)))
                .collect(Collectors.toList());
    }

    /**
     * Returns the amount of days that were played since the tournament started until
     * the given date.
     * If tournament has not started, returns 0.
     * @param date Date to count played days by.
     * @return Days played until date
     */
    public int getDaysPlayedAtDate(LocalDate date){
        TournamentStatus status = getStatusByDate(date);

        if (status == TournamentStatus.NOTSTARTED)
            return 0;
        else if (status == TournamentStatus.STARTED)
            return (int) startDate.datesUntil(date).count();
        else
            return (int) (startDate.datesUntil(endDate).count() + 1);
    }

    public boolean supportsLanguage(Language language) {
        return languages.contains(language);
    }
    
    public TournamentStatus getStatus() {
    	return getStatusByDate(LocalDate.now());
    }

    public TournamentStatus getStatusByDate(LocalDate date){
        if (date.isBefore(startDate))
            return TournamentStatus.NOTSTARTED;
        else if (date.isAfter(endDate))
            return TournamentStatus.FINISHED;
        else
            return TournamentStatus.STARTED;
    }
}

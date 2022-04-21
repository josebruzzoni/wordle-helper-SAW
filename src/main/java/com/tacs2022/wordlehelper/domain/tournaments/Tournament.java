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
import java.util.List;

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

    @ElementCollection
    private List<Language> languages = new ArrayList<>();
    private Visibility visibility;
    @ManyToMany
    private List<User> participants = new ArrayList<>();

    public Tournament(String name, LocalDate startDate, LocalDate endDate, List<Language> languages, Visibility visibility) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.languages.addAll(languages);
        this.visibility = visibility;
    }

    public Tournament(NewTournamentDto newTournamentDto) {
        this.name = newTournamentDto.getName();
        this.startDate = newTournamentDto.getStartDate();
        this.endDate = newTournamentDto.getEndDate();
        this.languages.addAll(newTournamentDto.getLanguages());
        this.visibility = newTournamentDto.getVisibility();
    }

    public void addParticipant(User newParticipant) {
        this.participants.add(newParticipant);
    }

    public Leaderboard generateLeaderboard(){
        //TODO: generate leaderboard
        return new Leaderboard();
    }

    public boolean endedToDate(LocalDate date) {
        return !endDate.isAfter(date);
    }

    public boolean startedToDate(LocalDate date) {
        return !startDate.isAfter(date);
    }
}

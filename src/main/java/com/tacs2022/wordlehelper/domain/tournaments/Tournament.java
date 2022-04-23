package com.tacs2022.wordlehelper.domain.tournaments;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
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

    public void addParticipant(User newParticipant) {
        this.participants.add(newParticipant);
    }

    public List<Scoreboard> generateLeaderboard(){
        return participants.stream()
                .map(this::getUserScoreboard)
                .sorted(Comparator.comparing(Scoreboard::getFailedAttempts))
                .collect(Collectors.toList());
    }

    public Scoreboard getUserScoreboard(User user){
        List<Result> results = user.getResults().stream().filter(this::resultApplies).collect(Collectors.toList());
        return new Scoreboard(user, results);
    }

    private boolean resultApplies(Result result){
        return languages.contains(result.getLanguage()) && inProgressToDate(result.getDate());
    }

    public boolean inProgressToDate(LocalDate date){
        return startedToDate(date) && !endedToDate(date);
    }

    public boolean endedToDate(LocalDate date) {
        return !endDate.isAfter(date);
    }

    public boolean startedToDate(LocalDate date) {
        return !startDate.isAfter(date);
    }
}

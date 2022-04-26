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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public boolean endedToDate(LocalDate date) {
        return date.isAfter(endDate);
    }

    public boolean startedToDate(LocalDate date) {
        return !startDate.isAfter(date);
    }

}

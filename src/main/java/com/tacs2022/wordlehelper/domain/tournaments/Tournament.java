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

    public List<Position> generateLeaderboard(){
        return participants.stream()
                .map(u -> new Position(u, scoreForUser(u)))
                .sorted(Comparator.comparing(Position::getFailedAttempts)) //TODO: Ver si hace falta invertir el orden
                .collect(Collectors.toList());
    }

    public int scoreForUser(User user){
        /*if(!user.isParticipant(this)){
            throw new NotParticipantException(user) //"It was attempted to consult the score of "+user.getUsername()+" who isn't a participant of this tournament
        }
        */
        return user.getResults().stream()
                .filter(this::resultApplies).map(Result::getFailedAttempts)
                .reduce(0, Integer::sum);
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

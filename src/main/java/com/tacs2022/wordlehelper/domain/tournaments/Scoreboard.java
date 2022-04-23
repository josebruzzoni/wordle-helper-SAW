package com.tacs2022.wordlehelper.domain.tournaments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Scoreboard {
    User user;
    @JsonIgnore
    Tournament tournament;
    @JsonIgnore
    List<Result> results;

    public Scoreboard(User user, Tournament tournament){
        this.user = user;
        this.tournament = tournament;
        refreshResults();
    }

    public int getBadScoreToDate(LocalDate date){

        int notPlayedDays = tournament.daysPassedToDate(date) - getPlayedGames();
        return 7*notPlayedDays + getFailedAttempts();
    }

    public int getFailedAttempts(){
        return getResults().stream().mapToInt(Result::getFailedAttempts).sum();
    }

    private int getPlayedGames(){
        return getResults().size();
    }

    private List<Result> getResults() {
        return this.results;
    }

    public void refreshResults(){
        this.results = user.getResults().stream().filter(tournament::considers).collect(Collectors.toList());
    }
}

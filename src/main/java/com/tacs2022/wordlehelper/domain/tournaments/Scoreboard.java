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

    private static final int NOT_PLAYED_PENALIZATION_ATTEMPTS = 7;

    User user;
    @JsonIgnore
    Tournament tournament;
    @JsonIgnore
    List<Result> results;

    public Scoreboard(User user, Tournament tournament){
        this.user = user;
        this.tournament = tournament;
        this.results = user.getResults().stream()  //of all the user results get
                .filter(result -> tournament.supportsLanguage(result.getLanguage())) //only results for the tournament language
                .filter(result -> tournament.getStatusByDate(result.getDate()) == TournamentStatus.STARTED) //only of dates where the tournament was STARTED
                .collect(Collectors.toList());
    }

    public int getCurrentScore(){
        return getScoreAtDate(LocalDate.now());
    }

    /**
     * Calculates the final score of the user's tournament results at a given date
     * The final score is the sum of the total attempts used to complete every day's game
     * with days not played by the user counting as 7 attempts.
     * @param date Date to calculate partial score.
     * @return The player's score by the given date
     */
    public int getScoreAtDate(LocalDate date){
        int notPlayedDays = Integer.max(0, tournament.getDaysPlayedAtDate(date) - getPlayedGames());
        return NOT_PLAYED_PENALIZATION_ATTEMPTS * notPlayedDays + getTotalAttempts();
    }

    /**
     * Calculates the total attempts of the user's tournament results
     * @return The sum of all attempts in this tournament for the days played
     */
    public int getTotalAttempts(){
        return results.stream().mapToInt(Result::getAttempts).sum();
    }

    private int getPlayedGames(){
        return results.size();
    }
}

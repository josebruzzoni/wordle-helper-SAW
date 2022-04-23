package com.tacs2022.wordlehelper.domain.tournaments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Data
public class Scoreboard {
    User user;

    @JsonIgnore
    List<Result> results;

    public String getUsername() {
        return user.getUsername(); //TODO refactor
    }

    public Scoreboard(User user, List<Result> results){
        this.user = user;
        this.results = results;
    }

    @Value("score")
    public int getScore(){
        return results.stream().mapToInt(Result::getScore).sum();
    }

    @Value("failedAttempts")
    public int getFailedAttempts(){
        return results.stream().mapToInt(Result::getFailedAttempts).sum();
    }
}

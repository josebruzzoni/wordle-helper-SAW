package com.tacs2022.wordlehelper.domain.tournaments;

import com.tacs2022.wordlehelper.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Position {
    User user;
    int failedAttempts;

    public String getUsername() {
        return user.getUsername(); //TODO refactor
    }


}

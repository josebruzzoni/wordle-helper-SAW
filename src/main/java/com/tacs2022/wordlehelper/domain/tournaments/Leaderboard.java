package com.tacs2022.wordlehelper.domain.tournaments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Leaderboard {
    //TODO
    private List<Object> leaderboard = new ArrayList<>();
}

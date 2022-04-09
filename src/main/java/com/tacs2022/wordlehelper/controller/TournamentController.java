package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import com.tacs2022.wordlehelper.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TournamentController {
    @Autowired
    TournamentService tournamentService;

    @GetMapping("/tournaments")
    public List<Tournament> getAllUsers() {
        return tournamentService.findAll();
    }
}

package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TournamentController {
    @Autowired
    TournamentService tournamentService;
    @Autowired
    UserService userService;

    @GetMapping("/tournaments")
    public List<Tournament> getAllUsers() {
        return tournamentService.findAll();
    }

    @GetMapping("/tournaments/{id}")
    public Tournament getTournamentById(@PathVariable(value = "id") Long id) {
        return tournamentService.findById(id);
    }

    @GetMapping("/tournaments/{id}/leaderboard")
    public List<Object> getLeaderboardOfTournament(@PathVariable(value = "id") Long id){
        return tournamentService.getLeaderboardOfTournament(id);
    }

    /*
    @PostMapping("/tournaments/{id}/participants")
    public void addParticipant(@RequestBody JSONObject jsonRequest, @PathVariable(value = "id") Long idTournament){

        Long id_long = ((Number) jsonRequest.get("participant_id")).longValue();
        User newParticipant = userService.findById(jsonRequest.);
        Tournament tournament = tournamentService.findById(idTournament);
        tournament.addParticipant(newParticipant);
    }

    @PostMapping("/tournaments")
    public Tournament postTournament(@RequestBody Tournament tournament){
        tournamentService.addTournament(tournament);
        return tournament;
    }*/
}

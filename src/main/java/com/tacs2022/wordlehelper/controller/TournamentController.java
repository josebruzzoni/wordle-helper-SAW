package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

	@PostMapping("/tournaments/{id}/participants")
    public ResponseEntity<Void> addParticipant(@RequestBody Map<String, Object> json, @PathVariable(value = "id") Long idTournament){

        Integer id = (Integer) json.getOrDefault("idParticipant", Integer.valueOf(1));
        User newParticipant = userService.findById(id.longValue());
        Tournament tournament = tournamentService.findById(idTournament);
        tournament.addParticipant(newParticipant);
        
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/tournaments")
    public Tournament postTournament(@RequestBody Tournament tournament){
        tournamentService.addTournament(tournament);
        return tournament;
    }
}

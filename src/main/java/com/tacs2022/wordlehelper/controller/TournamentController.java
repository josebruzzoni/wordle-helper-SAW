package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.tournaments.Leaderboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewParticipantDto;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputTournamentDto;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputTournamentsDto;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/tournaments")
@RestController()
public class TournamentController {
    @Autowired
    TournamentService tournamentService;
    @Autowired
    UserService userService;

    @GetMapping()
    public OutputTournamentsDto getAllTournaments(@RequestParam(required = false) String role, @RequestParam(required = false) String status) {
        return new OutputTournamentsDto(tournamentService.findAll(role, status));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OutputTournamentDto create(@Valid @RequestBody NewTournamentDto tournament, @RequestHeader(required = true) String Authorization){
    	String token = Authorization.substring(7);
    	Long userId = userService.getUserIdFromToken(token);
    	User owner = userService.findById(userId);
    	Tournament newTournament = new Tournament(tournament, owner);
        return new OutputTournamentDto(tournamentService.save(newTournament));
    }

    @GetMapping("/{id}")
    public OutputTournamentDto getTournamentById(@PathVariable(value = "id") Long id) {
        return new OutputTournamentDto(tournamentService.findById(id));
    }

    @GetMapping("/{id}/leaderboard")
    public Leaderboard getLeaderboardByTournamentId(@PathVariable(value = "id") Long tournamentId){
        return tournamentService.getTournamentLeaderboard(tournamentId);
    }

	@PostMapping(value="/{id}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addParticipant(@Valid @RequestBody NewParticipantDto body, @PathVariable(value = "id") Long tournamentId){
        tournamentService.addParticipant(tournamentId, userService.findById(body.getIdParticipant()));
    }

}

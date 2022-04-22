package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.controller.Exceptions.MissingAttributesException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

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
    public OutputTournamentDto create(@Valid @RequestBody NewTournamentDto tournament){
    	//TODO get user from token
    	User owner = userService.findById(Long.valueOf(1));
        return tournamentService.save(new Tournament(tournament, owner)).getResponse();
    }

    @GetMapping("/{id}")
    public OutputTournamentDto getTournamentById(@PathVariable(value = "id") Long id) {
        return tournamentService.findById(id).getResponse();
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

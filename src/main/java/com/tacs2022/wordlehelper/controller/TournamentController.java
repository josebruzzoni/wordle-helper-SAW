package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.dtos.tournaments.NewParticipantDto;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputTournamentsDto;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
    public Tournament create(@Valid @RequestBody NewTournamentDto tournament){
        return tournamentService.save(tournament.fromDTO());
    }

    @GetMapping("/{id}")
    public Tournament getTournamentById(@PathVariable(value = "id") Long id) {
        return tournamentService.findById(id);
    }

    @GetMapping("/{id}/leaderboard")
    public Map<String, List<Scoreboard>> getLeaderboardByTournamentId(@PathVariable(value = "id") Long tournamentId){
        Map<String, List<Scoreboard>> response = new HashMap<>();
        response.put(
               "leaderboard", tournamentService.getTournamentLeaderboard(tournamentId, LocalDate.now())
        );

        return response;
    }

	@PostMapping(value="/{id}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addParticipant(@Valid @RequestBody NewParticipantDto body, @PathVariable(value = "id") Long tournamentId){
        tournamentService.addParticipant(tournamentId, userService.findById(body.getIdParticipant()));
    }

}

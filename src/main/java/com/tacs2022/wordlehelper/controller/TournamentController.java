package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.JsonResponseDto;
import com.tacs2022.wordlehelper.dtos.tournaments.*;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("/v1/tournaments")
@RestController()
public class TournamentController {
    @Autowired
    TournamentService tournamentService;
    @Autowired
    UserService userService;

    @GetMapping()
    public JsonResponseDto getAllPublicTournaments() {
        return new JsonResponseDto("tournaments", OutputTournamentDto.list(tournamentService.findPublicTournaments()));
    }


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OutputTournamentDto create(@Valid @RequestBody NewTournamentDto tournament, @RequestHeader(required = true) String authorization){
    	User owner = userService.getUserFromAuth(authorization);
    	Tournament newTournament = tournament.asTournamentWithOwner(owner);
        return new OutputTournamentDto(tournamentService.save(newTournament));
    }

    @GetMapping("/{id}")
    public OutputTournamentDto getTournamentById(@PathVariable(value = "id") Long id, @RequestHeader(required = true) String authorization) {
        User user = userService.getUserFromAuth(authorization);
    	return new OutputTournamentDto(tournamentService.getByIdAndValidateVisibility(id, user));
    }

    @GetMapping("/{id}/leaderboard")
    public JsonResponseDto getLeaderboardByTournamentId(@PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String authorization){
    	User user = userService.getUserFromAuth(authorization);
        List<Scoreboard> scoreboards = tournamentService.getTournamentLeaderboard(tournamentId, LocalDate.now(), user);
        return new JsonResponseDto("leaderboard", OutputScoreboardDto.list(scoreboards));

    }

	@PostMapping(value="/{id}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addParticipant(@Valid @RequestBody NewParticipantDto body, @PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String authorization ){
		User postulator = userService.getUserFromAuth(authorization);
		User participant = userService.findByUsername(body.getParticipantName());
        tournamentService.addParticipant(tournamentId, postulator, participant);
    }

}

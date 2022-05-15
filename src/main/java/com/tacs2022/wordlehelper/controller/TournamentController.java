package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewParticipantDto;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputScoreboardsDto;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputTournamentDto;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputTournamentsDto;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RequestMapping("/v1/tournaments")
@RestController()
public class TournamentController {
    @Autowired
    TournamentService tournamentService;
    @Autowired
    UserService userService;

    @GetMapping()
    public OutputTournamentsDto getAllPublicTournaments() {
        return new OutputTournamentsDto(tournamentService.findPublicTournaments());
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OutputTournamentDto create(@Valid @RequestBody NewTournamentDto tournament, @RequestHeader(required = true) String authorization){
    	User owner = userService.getUserFromAuth(authorization);
    	Tournament newTournament = new Tournament(tournament, owner);
        return new OutputTournamentDto(tournamentService.save(newTournament));
    }

    @GetMapping("/{id}")
    public OutputTournamentDto getTournamentById(@PathVariable(value = "id") Long id, @RequestHeader(required = true) String authorization) {
        User user = userService.getUserFromAuth(authorization);
    	return new OutputTournamentDto(tournamentService.getByIdAndValidateVisibility(id, user));
    }

    @GetMapping("/{id}/leaderboard")
    public OutputScoreboardsDto getLeaderboardByTournamentId(@PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String authorization){
    	User user = userService.getUserFromAuth(authorization);
    	return new OutputScoreboardsDto(tournamentService.getTournamentLeaderboard(tournamentId, LocalDate.now(), user));
    }

	@PostMapping(value="/{id}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addParticipant(@Valid @RequestBody NewParticipantDto body, @PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String authorization ){
		User user = userService.getUserFromAuth(authorization); //user that requests the addition of participant to the tournament
		User participant = userService.findById(body.getIdParticipant()); //user that is added to the tournament
        tournamentService.addParticipant(tournamentId, user, participant);
    }

}

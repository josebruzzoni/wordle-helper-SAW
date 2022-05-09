package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
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

import java.util.List;

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
    public OutputTournamentsDto getAllTournaments( @RequestParam(required = false) TournamentStatus status, @RequestHeader(required = true) String Authorization) {
    	User user = userService.getUserFromToken(Authorization);
    	
    	List<Tournament> tournaments =  null;
    	
    	if(status == null) {
    		tournaments = tournamentService.findPublicTournamentsInwhichNotRegistered(user);
    	}else {
    		tournaments = tournamentService.findPublicTournamentsInwhichNotRegisteredByStatus(user, status);
    	}
    	
    	return new OutputTournamentsDto(tournaments);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OutputTournamentDto create(@Valid @RequestBody NewTournamentDto tournament, @RequestHeader(required = true) String Authorization){
    	User owner = userService.getUserFromToken(Authorization);
    	Tournament newTournament = new Tournament(tournament, owner);
        return new OutputTournamentDto(tournamentService.save(newTournament));
    }

    @GetMapping("/{id}")
    public OutputTournamentDto getTournamentById(@PathVariable(value = "id") Long id, @RequestHeader(required = true) String Authorization) {
        User user = userService.getUserFromToken(Authorization);
    	return new OutputTournamentDto(tournamentService.getByIdAndValidateVisibility(id, user));
    }

    @GetMapping("/{id}/leaderboard")
    public OutputScoreboardsDto getLeaderboardByTournamentId(@PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String Authorization){
    	User user = userService.getUserFromToken(Authorization);
    	return new OutputScoreboardsDto(tournamentService.getTournamentLeaderboard(tournamentId, LocalDate.now(), user));
    }

	@PostMapping(value="/{id}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addParticipant(@Valid @RequestBody NewParticipantDto body, @PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String Authorization ){
		User postulator = userService.getUserFromToken(Authorization);
		User participant = userService.findById(body.getIdParticipant());
        tournamentService.addParticipant(tournamentId, postulator, participant);
    }

}

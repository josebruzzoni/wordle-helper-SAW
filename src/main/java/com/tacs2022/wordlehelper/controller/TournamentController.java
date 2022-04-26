package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.tournaments.Leaderboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentType;
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

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

@RequestMapping("/tournaments")
@RestController()
public class TournamentController {
    @Autowired
    TournamentService tournamentService;
    @Autowired
    UserService userService;

    @GetMapping()
    public OutputTournamentsDto getAllTournaments(@RequestParam(required = false) TournamentType role, @RequestParam(required = false) TournamentStatus status, @RequestHeader(required = true) String Authorization) {
    	User user = userService.getUserFromToken(Authorization);
    	List<Tournament> tournaments =  null;
    	//change role for type. Values = (PUBLIC, REGISTERED, SELF)
    	if(role != null && status != null) {
    		tournaments = tournamentService.findByTypeAndStatus(role, status, user);
        } else if( role != null ) {
        	tournaments = tournamentService.findByType(role, user);
        } else if( status != null) {
        	tournaments = tournamentService.findByStatus(status, user);
        } else {
        	tournaments = tournamentService.findAllByUser(user);
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
    public Leaderboard getLeaderboardByTournamentId(@PathVariable(value = "id") Long tournamentId){
        return tournamentService.getTournamentLeaderboard(tournamentId);
    }

	@PostMapping(value="/{id}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addParticipant(@Valid @RequestBody NewParticipantDto body, @PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String Authorization ){
		User postulator = userService.getUserFromToken(Authorization);
		User participant = userService.findById(body.getIdParticipant());
        tournamentService.addParticipant(tournamentId, postulator, participant);
    }

}

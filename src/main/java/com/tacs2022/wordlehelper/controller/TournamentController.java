package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.JsonResponseDto;
import com.tacs2022.wordlehelper.dtos.tournaments.*;
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
    public JsonResponseDto getAllTournaments(@RequestParam(required = false) TournamentStatus status, @RequestHeader(required = true) String Authorization) {
    	User user = userService.getUserFromToken(Authorization);
    	
//    	List<Tournament> tournaments =  null;
//
//    	if(status == null) {
//    		tournaments = tournamentService.findPublicTournamentsInwhichNotRegistered(user);
//    	}else {
//    		tournaments = tournamentService.findPublicTournamentsInwhichNotRegisteredByStatus(user, status);
//    	}
        List<Tournament> tournaments = tournamentService.findAll();
    	
    	return new JsonResponseDto("tournaments", OutputTournamentDto.list(tournaments));
    }


    //TODO: tiene sentido mandar los results en user siempre?? o solo cuando estamos en la pag de results????????

    //TODO: testear si con settear el json ignore en user se puede hacer lo de OutputUserDto, y si es en cascada
    // para cualquier otro dto que tenga user o lo tengo que transformar a mano en ese dto tambien

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OutputTournamentDto create(@Valid @RequestBody NewTournamentDto tournament, @RequestHeader(required = true) String Authorization){
    	User owner = userService.getUserFromToken(Authorization);
    	Tournament newTournament = tournament.asTournamentWithOwner(owner);
        return new OutputTournamentDto(tournamentService.save(newTournament));
    }

    @GetMapping("/{id}")
    public OutputTournamentDto getTournamentById(@PathVariable(value = "id") Long id, @RequestHeader(required = true) String Authorization) {
        User user = userService.getUserFromToken(Authorization);
    	return new OutputTournamentDto(tournamentService.getByIdAndValidateVisibility(id, user));
    }

    @GetMapping("/{id}/leaderboard")
    public JsonResponseDto getLeaderboardByTournamentId(@PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String Authorization){
    	User user = userService.getUserFromToken(Authorization);
        List<Scoreboard> scoreboards = tournamentService.getTournamentLeaderboard(tournamentId, LocalDate.now(), user);
        return new JsonResponseDto("leaderboard", OutputScoreboardDto.list(scoreboards));
    }

	@PostMapping(value="/{id}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addParticipant(@Valid @RequestBody NewParticipantDto body, @PathVariable(value = "id") Long tournamentId, @RequestHeader(required = true) String Authorization ){
		User postulator = userService.getUserFromToken(Authorization);
		User participant = userService.findById(body.getIdParticipant());
        tournamentService.addParticipant(tournamentId, postulator, participant);
    }

}

package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import com.tacs2022.wordlehelper.service.Exceptions.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

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
    public Map<String, Object> getLeaderboardOfTournament(@PathVariable(value = "id") Long id){
    	Map<String, Object> json  = new HashMap<String, Object>();
    	json.put("leaderboard", tournamentService.getLeaderboardOfTournament(id));
        return json;
    }

	@PostMapping("/tournaments/{id}/participants")
	@Transactional
    public ResponseEntity<Void> addParticipant(@RequestBody Map<String, Object> json, @PathVariable(value = "id") Long idTournament){

        Integer id;
        ResponseEntity<Void> response;
        
        try {
        	id = (Integer) json.get("idParticipant");
        	User newParticipant = userService.findById(id.longValue());
            Tournament tournament = tournamentService.findById(idTournament);
            tournament.addParticipant(newParticipant);
            response = ResponseEntity.noContent().build();
		} catch (NotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.badRequest().build();
		}
        
        return response;
    }

    @PostMapping("/tournaments")
    public Tournament postTournament(@RequestBody Tournament tournament){
        tournamentService.addTournament(tournament);
        return tournament;
    }
}

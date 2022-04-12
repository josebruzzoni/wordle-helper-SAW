package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@RequestMapping("/tournaments")
@RestController()
public class TournamentController {
    @Autowired
    TournamentService tournamentService;
    @Autowired
    UserService userService;

    @GetMapping()
    public List<Tournament> getAllTournaments() {
        return tournamentService.findAll();
    }

    @PostMapping()
    @Transactional
    public ResponseEntity<Tournament> postTournament(@RequestBody Tournament tournament){
        tournamentService.addTournament(tournament);
        return ResponseEntity.ok(tournament);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(tournamentService.findById(id));
    }

    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<Map<String, Object>> getLeaderboardOfTournament(@PathVariable(value = "id") Long id){
    	Map<String, Object> body  = new HashMap<String, Object>();
    	body.put("leaderboard", tournamentService.getTournamentLeaderboard(id));
        return ResponseEntity.ok(body);
    }

	@PostMapping("/{id}/participants")
	@Transactional
    public ResponseEntity<Map<String, String>> addParticipant(@RequestBody Map<String, Long> body, @PathVariable(value = "id") Long idTournament){
        Tournament tournament = tournamentService.findById(idTournament);

        Long idParticipant = body.get("idParticipant");

        if(idParticipant==null){ //TODO: Manejar con excepcion
            Map<String, String> missingAttributes = new HashMap<>();
            missingAttributes.put("missingAttributes", "idParticipant");
            return ResponseEntity.badRequest().body(missingAttributes);
        }

        tournament.addParticipant(userService.findById(idParticipant));
        return ResponseEntity.noContent().build();
    }

}

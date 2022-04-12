package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.service.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TournamentService {
    @Autowired
    TournamentRepository tournamentRepo;

    public List<Tournament> findAll() {
        return (List<Tournament>) tournamentRepo.findAll();
    }

    public Tournament findById(Long id) {
        return tournamentRepo.findById(id).orElseThrow(
                () -> new NotFoundException("No tournament with id "+id+" was found")
        );
    }

    @Transactional
    public void addTournament(Tournament tournament) {
        tournamentRepo.save(tournament);
    }

    public List<Object> getLeaderboardOfTournament(Long id) {
//        return findById(id).getLeaderboard();
        findById(id);
        return new ArrayList<>();
    }

}

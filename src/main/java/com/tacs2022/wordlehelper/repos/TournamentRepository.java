package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import org.springframework.data.repository.CrudRepository;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {
}

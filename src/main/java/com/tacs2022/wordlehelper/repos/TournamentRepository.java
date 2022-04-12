package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import org.springframework.data.repository.CrudRepository;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {
}

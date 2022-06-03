package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TournamentRepository extends MongoRepository<Tournament, String> {

	List<Tournament> findByVisibility(Visibility visibility);
	
	@Query (value = "SELECT * FROM TOURNAMENT t INNER JOIN TOURNAMENT_PARTICIPANTS p ON t.ID = p.TOURNAMENT_ID WHERE p.PARTICIPANTS_ID = :userId")
	List<Tournament> findTournamentsInWhichUserIsRegistered(@Param("userId") String userId);
}

package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {

	List<Tournament> findByVisibility(Visibility visibility);
	
	@Query (value = "SELECT * FROM TOURNAMENT t INNER JOIN TOURNAMENT_PARTICIPANTS p ON t.ID = p.TOURNAMENT_ID WHERE p.PARTICIPANTS_ID = :userId", nativeQuery =true)
	List<Tournament> findTournamentsInWhichUserIsRegistered(@Param("userId") Long userId);
}

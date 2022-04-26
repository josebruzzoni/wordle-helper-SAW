package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {

	List<Tournament> findByOwner(User user);

	List<Tournament> findByVisibility(Visibility visibility);
	
	@Query (value = "SELECT * FROM TOURNAMENT t INNER JOIN TOURNAMENT_PARTICIPANTS p ON t.ID = p.TOURNAMENT_ID WHERE p.PARTICIPANTS_ID = :userId", nativeQuery =true)
	List<Tournament> findTournamentsInWhichUserIsRegistered(@Param("userId") Long userId);
	
	@Query( value = "SELECT * FROM TOURNAMENT WHERE CAST(:today AS date) < CAST(START_DATE AS date)", nativeQuery = true )
	List<Tournament> findUnstartedTournaments(@Param("today") LocalDate today);
	
	@Query( value = "SELECT * FROM TOURNAMENT WHERE CAST(:today AS date) >= CAST(START_DATE AS date) AND CAST(:today AS date) <= CAST(END_DATE AS date)", nativeQuery = true)
	List<Tournament> findStartedTournaments(@Param("today") LocalDate today);
	
	@Query( value = "SELECT * FROM TOURNAMENT WHERE CAST(:today AS date) > CAST(END_DATE AS date)", nativeQuery = true )
	List<Tournament> findFinishedTournaments(@Param("today") LocalDate today);
}

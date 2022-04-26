package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.utils.QueryUtils;

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
	
	//-----------------------------------------
	
	@Query( value = QueryUtils.TOURNAMENT_REGISTERED + QueryUtils.TOURNAMENT_CONDITION_SELF + QueryUtils.TOURNAMENT_CONDITION_NOT_STARTED , nativeQuery = true )
	List<Tournament> findMyUnstartedTournaments(@Param("userId") Long userId, @Param("today") LocalDate today);
	
	@Query( value = QueryUtils.TOURNAMENT_REGISTERED + QueryUtils.TOURNAMENT_CONDITION_NOT_STARTED , nativeQuery = true )
	List<Tournament> findMyUnstartedRegisteredTournaments(@Param("userId") Long userId, @Param("today") LocalDate today);
	
	@Query( value = QueryUtils.TOURNAMENT_REGISTERED + QueryUtils.TOURNAMENT_CONDITION_SELF + QueryUtils.TOURNAMENT_CONDITION_STARTED , nativeQuery = true )
	List<Tournament> findMyStartedTournaments(@Param("userId") Long userId, @Param("today") LocalDate today);
	
	@Query( value = QueryUtils.TOURNAMENT_REGISTERED + QueryUtils.TOURNAMENT_CONDITION_STARTED , nativeQuery = true )
	List<Tournament> findMyStartedRegisteredTournaments(@Param("userId") Long userId, @Param("today") LocalDate today);
	
	@Query( value = QueryUtils.TOURNAMENT_REGISTERED + QueryUtils.TOURNAMENT_CONDITION_SELF + QueryUtils.TOURNAMENT_CONDITION_FINISHED , nativeQuery = true )
	List<Tournament> findMyFinishedTournaments(@Param("userId") Long userId, @Param("today") LocalDate today);
	
	@Query( value = QueryUtils.TOURNAMENT_REGISTERED + QueryUtils.TOURNAMENT_CONDITION_FINISHED , nativeQuery = true )
	List<Tournament> findMyFinishedRegisteredTournaments(@Param("userId") Long userId, @Param("today") LocalDate today);
	
	
	
	
	
	
	
	
	
	
	
	
}

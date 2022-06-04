package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TournamentRepository extends MongoRepository<Tournament, String> {

	List<Tournament> findByVisibility(Visibility visibility);
	
	@Query(value="{ 'participants': { $elemMatch: { _id: ObjectId(?0) } } }")
	List<Tournament> findTournamentsInWhichUserIsRegistered(String userId);
}

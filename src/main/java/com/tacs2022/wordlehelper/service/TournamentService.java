package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.exceptions.ForbiddenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class TournamentService {
    @Autowired
    TournamentRepository tournamentRepo;
    
    Logger logger = LoggerFactory.getLogger(TournamentService.class);

	@Transactional
	public Tournament save(Tournament tournament) {
		tournamentRepo.save(tournament);
		return tournament;
	}

    public Tournament findById(Long id) {
        return tournamentRepo.findById(id).orElseThrow(
                () -> new NotFoundException("No tournament with id "+id+" was found")
        );
    }

	/**
	 * Finds a tournament by id, validating that the user requesting the tournament
	 * is participating (if it is a private tournament).
	 *
	 * @param id Tournament id
	 * @param user User that makes the request
	 * @return Tournament that matches given id
	 */
    public Tournament getByIdAndValidateVisibility(Long id, User user) {
    	Tournament tournament = findById(id);
    	
    	if(tournament.getVisibility().equals(Visibility.PRIVATE) && !tournament.userIsOwner(user) && !tournament.hasParticipant(user)) {
    		logger.info("User does not have permissions to view this tournament");
        	throw new ForbiddenException("User does not have permissions to view this tournament");
    	}
    	
    	return tournament;
    }


    public List<Scoreboard> getTournamentLeaderboard(Long id, LocalDate date, User user) {
        return getByIdAndValidateVisibility(id, user).generateLeaderboardAtDate(date);
    }

	/**
	 * Adds a User participant to the given tournament.
	 * If a tournament is private, only the owner is allowed to add participants
	 * If a tournament is public, both the owner and the participant itself can add the participant to the tournament
	 *
	 * For both cases, a participant can be added to a tournament only if it hasn't started yet.
	 *
	 * @param tournamentId ID of tournament to add participant
	 * @param user User that is trying to perform the action
	 * @param participant User to add to the tournament
	 */
    @Transactional
    public void addParticipant(Long tournamentId, User user, User participant) {
        Tournament tournament = findById(tournamentId);
        
        TournamentStatus status = tournament.getStatus();

		//check tournament has not started
        if(status.equals(TournamentStatus.STARTED) || status.equals(TournamentStatus.FINISHED)){
        	logger.info("User tried to add participant to a tournament with status {}", status);
            throw new ForbiddenException("Participants cannot be added to this tournament once it has started or finished");
        }

		//check user is owner of private tournament
        if(tournament.getVisibility().equals(Visibility.PRIVATE) && !tournament.userIsOwner(user)) {
        	logger.info("User tried to add participant to private tournament without being the owner");
        	throw new ForbiddenException("User cannot add participant to this private tournament without being the owner");
        }

		//check if public tournament and user trying to add another participant while not being owner
        if(tournament.getVisibility().equals(Visibility.PUBLIC) && !user.equals(participant) && !tournament.userIsOwner(user)) {
        	logger.info("User tried to add participant to public tournament without being the owner, can only add self");
        	throw new ForbiddenException("User can only add another participant to public tournament if owner");
        }

		//TODO: Alguna response distinta aca por ahi??
        if(!tournament.hasParticipant(participant))
        	tournament.addParticipant(participant);
    }


	public List<Tournament> findTournamentsInWhichUserIsRegistered(User user) {
		return tournamentRepo.findTournamentsInWhichUserIsRegistered(user.getId());
	}

	public List<Tournament> findPublicTournaments(){
		return tournamentRepo.findByVisibility(Visibility.PUBLIC);
	}
}

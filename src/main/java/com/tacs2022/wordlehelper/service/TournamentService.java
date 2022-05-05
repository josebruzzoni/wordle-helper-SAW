package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentType;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.ExpiredRequestException;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.exceptions.ForbiddenException;
import com.tacs2022.wordlehelper.utils.QueryUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentService {
    @Autowired
    TournamentRepository tournamentRepo;
    
    Logger logger = LoggerFactory.getLogger(TournamentService.class);

    public List<Tournament> findAll(){
        return (List<Tournament>) tournamentRepo.findAll();
    }

    public Tournament findById(Long id) {
        return tournamentRepo.findById(id).orElseThrow(
                () -> new NotFoundException("No tournament with id "+id+" was found")
        );
    }
    
    public Tournament getByIdAndValidateVisibility(Long id, User user) {
    	Tournament tournament = findById(id);
    	
    	if(tournament.getVisibility().equals(Visibility.PRIVATE) && !tournament.userIsOwner(user) && !tournament.hasParticipant(user)) {
    		logger.info("User does not have permissions to view this tournament");
        	throw new ForbiddenException("User does not have permissions to view this tournament");
    	}
    	
    	return tournament;
    }

    @Transactional
    public Tournament save(Tournament tournament) {
        tournamentRepo.save(tournament);
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

	public List<Tournament> findByTypeAndStatus(TournamentType type, TournamentStatus status, Long userId) {
		List<Tournament> tournament;
		
		if(type.equals(TournamentType.REGISTERED)) {
			tournament = findByStatus(status, userId);
		}else {
			tournament = findMyTournamentsByStatus(status, userId);
		}
		
		return tournament;
	}

	public List<Tournament> findByType(TournamentType type, User user) {
		List<Tournament> tournament;
		
		try {
			switch(type) {
				case SELF:
					tournament = tournamentRepo.findByOwner(user);
					break;
				case REGISTERED:
					tournament = tournamentRepo.findTournamentsInWhichUserIsRegistered(user.getId());
					break;
				default:
					tournament = new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error while retrieving tournaments from database");
			tournament = new ArrayList<>();
		}
		
		return tournament;
	}

	public List<Tournament> findByStatus(TournamentStatus status, Long userId) {
		List<Tournament> tournament;
		LocalDate today = LocalDate.now();
		try {
			switch(status) {
				case NOTSTARTED:
					System.out.println(userId);
					System.out.println(QueryUtils.TOURNAMENT_REGISTERED + QueryUtils.TOURNAMENT_CONDITION_NOT_STARTED);
					tournament = tournamentRepo.findMyUnstartedRegisteredTournaments(userId, today);
					break;
				case STARTED:
					tournament = tournamentRepo.findMyStartedRegisteredTournaments(userId, today);
					break;
				case FINISHED:
					tournament = tournamentRepo.findMyFinishedRegisteredTournaments(userId, today);
					break;
				default:
					tournament = new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error while retrieving tournaments from database");
			tournament = new ArrayList<>();
		}
		
		return tournament;
	}
	
	private List<Tournament> findMyTournamentsByStatus(TournamentStatus status, Long userId) {
		List<Tournament> tournament;
		LocalDate today = LocalDate.now();
		try {
			switch(status) {
				case NOTSTARTED:
					tournament = tournamentRepo.findMyUnstartedTournaments(userId, today);
					break;
				case STARTED:
					tournament = tournamentRepo.findMyStartedTournaments(userId, today);
					break;
				case FINISHED:
					tournament = tournamentRepo.findMyFinishedTournaments(userId, today);
					break;
				default:
					tournament = new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error while retrieving tournaments from database");
			tournament = new ArrayList<>();
		}
		
		return tournament;
	}

	public List<Tournament> findTournamentsInWhichUserIsRegistered(User user) {
		return tournamentRepo.findTournamentsInWhichUserIsRegistered(user.getId());
	}
	
	public List<Tournament> findPublicTournamentsInwhichNotRegistered(User user) {
		List<Tournament> publics = tournamentRepo.findByVisibility(Visibility.PUBLIC);
		return publics.stream()
				.filter( (Tournament tournament) -> !tournament.hasParticipant(user) )
				.collect(Collectors.toList()); //TODO hacer un query en el repo de tournaments
	}
	
	public List<Tournament> findPublicTournamentsInwhichNotRegisteredByStatus(User user, TournamentStatus status) {
		return findPublicTournamentsInwhichNotRegistered(user)
				.stream()
				.filter((Tournament tournament) -> tournament.getStatus().equals(status))
				.collect(Collectors.toList()); //TODO hacer un query en el repo de tournaments
	}
}

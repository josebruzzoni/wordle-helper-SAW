package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.controller.Exceptions.ExpiredRequestException;
import com.tacs2022.wordlehelper.domain.tournaments.Leaderboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentType;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.service.exceptions.ForbiddenException;
import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.utils.QueryUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    	
    	if(tournament.isPrivate() && !tournament.isOwner(user) && !tournament.isAParticipant(user)) {
    		logger.info("El usuario no tiene permisos para ver este torneo");
        	throw new ForbiddenException("El usuario no tiene permisos para ver este torneo");
    	}
    	
    	return tournament;
    }

    @Transactional
    public Tournament save(Tournament tournament) {
        tournamentRepo.save(tournament);
        return tournament;
    }

    public Leaderboard getTournamentLeaderboard(Long id) {
        return findById(id).generateLeaderboard();
    }

    @Transactional
    public void addParticipant(Long tournamentId, User postulator, User participant) {
        Tournament tournament = findById(tournamentId);
        
        TournamentStatus status = tournament.getStatus();
        
        if(status.equals(TournamentStatus.STARTED) || status.equals(TournamentStatus.FINISHED)){
        	logger.info("Intento agregar a un usuario a un torneo que ya empezo o ya ha finalizado");
            throw new ExpiredRequestException();
        }
        
        if(tournament.isPrivate() && !tournament.isOwner(postulator)) {
        	logger.info("Intento agregar un usuario a un torneo privado sin ser owner");
        	throw new ForbiddenException("No podes agregar usuario a este torneo porque es privado");
        }
        
        if(!tournament.isPrivate() && !postulator.equals(participant)) {
        	logger.info("Intento agregar otro usuario a un torneo publico, solo puede unirse a si mismo");
        	throw new ForbiddenException("No podes agregar a otro usuario a este torneo, solo podes unirte vos mismo");
        }
        
        if(!tournament.isAParticipant(participant))
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
			logger.error("Error al intentar obtener los torneos desde la base de datos");
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
			logger.error("Error al intentar obtener los torneos desde la base de datos");
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
			logger.error("Error al intentar obtener los torneos desde la base de datos");
			tournament = new ArrayList<>();
		}
		
		return tournament;
	}

	public List<Tournament> findTournamentsInWhichUserIsRegistered(User user) {
		return tournamentRepo.findTournamentsInWhichUserIsRegistered(user.getId());
	}
}

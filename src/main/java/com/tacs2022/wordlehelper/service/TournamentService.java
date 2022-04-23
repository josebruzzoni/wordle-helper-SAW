package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.controller.Exceptions.ExpiredRequestException;
import com.tacs2022.wordlehelper.domain.tournaments.Scoreboard;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.repos.TournamentRepository;
import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TournamentService {
    @Autowired
    TournamentRepository tournamentRepo;

    public List<Tournament> findAll(){
        return (List<Tournament>) tournamentRepo.findAll();
    }

    public List<Tournament> findAll(String role, String status) {

        return findAll().stream()
                .filter(filterForRole(role).and(filterForStatus(status)))
                .collect(Collectors.toList());
    }

    public Tournament findById(Long id) {
        return tournamentRepo.findById(id).orElseThrow(
                () -> new NotFoundException("No tournament with id "+id+" was found")
        );
    }

    @Transactional
    public Tournament save(Tournament tournament) {
        tournamentRepo.save(tournament);
        return tournament;
    }

    public List<Scoreboard> getTournamentLeaderboard(Long id, LocalDate date) {
        return findById(id).generateLeaderboardToDate(date);
    }

    //******************************************************************************************
    //auxiliares - TODO: Ver si conviene mover a clase aparte
    public Predicate<Tournament> filterForStatus(String status){
        LocalDate today = LocalDate.now();
        return status==null? __ -> true
                :  status.equalsIgnoreCase("InPROGRESS")?    t-> t.inProgressToDate(today)
                :  status.equalsIgnoreCase("NotStarted")? t->!t.startedToDate(today)
                :  status.equalsIgnoreCase("Ended")?      t-> t.endedToDate(today)
                :  __ -> false //TODO: A valor absurdo no devuelvo nada. Ver como validar
                ;
    }

    private Predicate<Tournament> filterForRole(String role){
        User user = new User(); //TODO: Ver como sacar al usuario y si vale la pena que sea enum
        return role==null? t -> t.getVisibility().equals(Visibility.PUBLIC)
//                : role.equalsIgnoreCase("OWNER")? user::isOwner
//                : role.equalsIgnoreCase("PARTICIPANT")? user::isParticipant
                : __ -> false
                ;
    }

    @Transactional
    public void addParticipant(Long tournamentId, User byId) {
        Tournament tournament = findById(tournamentId);
        if(tournament.startedToDate(LocalDate.now())){
            throw new ExpiredRequestException();
        }

        tournament.addParticipant(byId);
    }
}

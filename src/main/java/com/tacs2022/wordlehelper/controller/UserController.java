package com.tacs2022.wordlehelper.controller;

import java.util.List;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.JsonResponseDto;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputTournamentDto;
import com.tacs2022.wordlehelper.dtos.user.NewUserDto;
import com.tacs2022.wordlehelper.exceptions.ForbiddenException;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    @Autowired
    UserService userService;
    
    @Autowired
    TournamentService tournamentService;

    @GetMapping()
    public JsonResponseDto getAllUsers() {
        return new JsonResponseDto("users", userService.findAll());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.save(newUserDto.getUsername(), newUserDto.getPassword());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(value = "id") String id) {
        return userService.findById(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "id") String id) {
        userService.delete(id);
    }
    
    @PostMapping("{userId}/results")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addUserResults(@Valid @RequestBody Result savedResult, @PathVariable(value = "userId") String userId){
        userService.addResult(userId, savedResult);
    }
    
    @GetMapping("{userId}/tournaments")
    public JsonResponseDto getTournaments(@PathVariable(value = "userId") String userId, @RequestHeader(required = true) String authorization) {
    	User user = userService.getUserFromAuth(authorization);

    	if(!user.getId().equals(userId)) {
    		throw new ForbiddenException("A user cannot view other users tournaments");
    	}

    	List<Tournament> tournaments = tournamentService.findTournamentsInWhichUserIsRegistered(user);
    	return new JsonResponseDto("tournaments", OutputTournamentDto.list(tournaments));
    }
}

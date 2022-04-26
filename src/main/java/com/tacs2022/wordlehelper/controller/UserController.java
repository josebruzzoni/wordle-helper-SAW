package com.tacs2022.wordlehelper.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.stream.Collectors;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentType;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.OutputTournamentsDto;
import com.tacs2022.wordlehelper.dtos.user.NewResultDto;
import com.tacs2022.wordlehelper.dtos.user.NewUserDto;
import com.tacs2022.wordlehelper.dtos.user.OutputUserDto;
import com.tacs2022.wordlehelper.dtos.user.OutputUsersDto;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;
    
    @Autowired
    TournamentService tournamentService;

    @GetMapping()
    public OutputUsersDto getAllUsers() {
        List<User> users = userService.findAll();
        List<OutputUserDto> userDtos = users.stream().map(u -> new OutputUserDto(u)).collect(Collectors.toList());
        return new OutputUsersDto(userDtos);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OutputUserDto create(@Valid @RequestBody NewUserDto newUserDto) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User user = userService.save(newUserDto.getUsername(), newUserDto.getPassword());
        return new OutputUserDto(user);
    }

    @GetMapping("/{id}")
    public OutputUserDto getUserById(@PathVariable(value = "id") Long id) {
        return new OutputUserDto(userService.findById(id));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "id") Long id) {
        userService.delete(id);
    }
    
    @PostMapping("{userId}/results")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addUserResults(@Valid @RequestBody NewResultDto result, @PathVariable(value = "userId") Long userId){
        userService.addResult(userId, new Result(result));
    }
    
    @GetMapping("{userId}/tournaments")
    public OutputTournamentsDto getTournaments(@PathVariable(value = "userId") Long userId, @RequestParam(required = false) TournamentType type, @RequestParam(required = false) TournamentStatus status, @RequestHeader(required = true) String Authorization) {
    	User user = userService.getUserFromToken(Authorization);
    	
    	if(user.getId() != userId) {
    		throw new NotFoundException("El usuario no puede ver los torneos de otro usuario");
    	}
    	
    	List<Tournament> tournaments =  null;
    	
    	if(type != null && status != null) {
    		tournaments = tournamentService.findByTypeAndStatus(type, status, user.getId());
        } else if( type != null ) {
        	tournaments = tournamentService.findByType(type, user);
        } else if( status != null) {
        	tournaments = tournamentService.findByStatus(status, user.getId());
        } else {
        	tournaments = tournamentService.findTournamentsInWhichUserIsRegistered(user);
        }
    	
    	return new OutputTournamentsDto(tournaments);
    }
}

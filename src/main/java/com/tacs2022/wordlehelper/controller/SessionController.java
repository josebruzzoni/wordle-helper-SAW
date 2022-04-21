package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.controller.exceptions.InvalidUserException;
import com.tacs2022.wordlehelper.dtos.user.NewUserDto;
import com.tacs2022.wordlehelper.dtos.user.OutputSessionDto;
import com.tacs2022.wordlehelper.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    @Autowired
    SessionService sessionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OutputSessionDto login(@Valid @RequestBody NewUserDto body) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String token = sessionService.getToken(body.getUsername(), body.getPassword());
        if (token == null){
            throw new InvalidUserException();
        }
        return new OutputSessionDto(token);
    }

    @DeleteMapping(path = "/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@PathVariable(value = "token") String token) {
        sessionService.removeToken(token);
    }
}

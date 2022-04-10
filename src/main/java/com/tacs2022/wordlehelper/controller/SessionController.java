package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.dtos.AuthDto;
import com.tacs2022.wordlehelper.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
    @Autowired
    SessionService sessionService;

    @PostMapping(path = "/sessions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody AuthDto authDto) {
        String token = sessionService.getToken(authDto);

        return new ResponseEntity(token, HttpStatus.OK);
    }

    @DeleteMapping(path = "/sessions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity logout(@RequestBody AuthDto authDto) {
        String token = sessionService.getToken(authDto);

        return new ResponseEntity(token, HttpStatus.OK);
    }
}

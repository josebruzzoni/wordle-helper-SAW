package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.dtos.AuthDto;
import com.tacs2022.wordlehelper.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    @Autowired
    SessionService sessionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> login(@RequestBody AuthDto authDto) {
        String token = sessionService.getToken(authDto);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }

    @DeleteMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody String token) {
        sessionService.removeToken(token);
    }
}

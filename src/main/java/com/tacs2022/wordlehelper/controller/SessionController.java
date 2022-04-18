package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    @Autowired
    SessionService sessionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = body.get("username");
        String password = body.get("password");
        String token = sessionService.getToken(username, password);
        if (token == null){
            Map<String, String> outputBody = new HashMap<>();
            outputBody.put("message", "Invalid username or password");
            return ResponseEntity.badRequest().body(outputBody);
        }
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.created(null).body(response);
    }

    @DeleteMapping(path = "/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@PathVariable(value = "token") String token) {
        sessionService.removeToken(token);
    }
}

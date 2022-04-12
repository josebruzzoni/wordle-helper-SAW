package com.tacs2022.wordlehelper.controller;

import java.util.List;
import java.util.Map;

import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.domain.Tournaments.Tournament;
import com.tacs2022.wordlehelper.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable(value = "id") Long id) {
        return userService.findById(id);
    }
    
    @PostMapping("/users/{idUser}/results")
    public ResponseEntity<Object> postTournament(@RequestBody Map<String, Object> json, @PathVariable(value = "idUser") Long idTournament){
        return ResponseEntity.noContent().build();
    }
    
    
}

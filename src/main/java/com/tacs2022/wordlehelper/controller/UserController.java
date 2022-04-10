package com.tacs2022.wordlehelper.controller;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    // users endpoints

    @GetMapping("/users")
    public Map<String,List<User>> getAllUsers() {
        Map<String,List<User>> users = new HashMap<>();
        users.put("users", userService.findAll());
        return users;
    }


    @PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@RequestBody User newUser){
        User user = userService.save(newUser);
        return new ResponseEntity(user, HttpStatus.CREATED);
    }

    // users/{id} endpoints

    @GetMapping("/users/{id}")
    public Map<String,User> getUserById(@PathVariable(value = "id") Long id) {
        Map<String,User> user = new HashMap<>();
        user.put("user", userService.findById(id));
        return user;
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity update(@RequestBody User existingUser) {
        userService.update(existingUser);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity delete(@PathVariable(value = "id") Long id) {
        userService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}

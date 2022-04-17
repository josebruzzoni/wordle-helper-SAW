package com.tacs2022.wordlehelper.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping()
    public Map<String, List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        Map<String, List<User>> response = new HashMap<>();
        response.put("users", users);
        return response;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody Map<String, String> body) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = body.get("username");
        String password = body.get("password");
        return userService.save(username, password);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(value = "id") Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody User existingUser) {
        userService.update(existingUser);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "id") Long id) {
        userService.delete(id);
    }
    
    @PostMapping("{id}/results")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addUserResults(@RequestBody Result result, @PathVariable(value = "userId") Long userId){
        userService.addResult(userId, result);
    }
    
    
}

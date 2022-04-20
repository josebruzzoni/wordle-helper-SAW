package com.tacs2022.wordlehelper.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.user.NewUserDto;
import com.tacs2022.wordlehelper.dtos.user.OutputUserDto;
import com.tacs2022.wordlehelper.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping()
    public Map<String, List<OutputUserDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<OutputUserDto> userDtos = users.stream().map(u -> new OutputUserDto(u)).collect(Collectors.toList());
        Map<String, List<OutputUserDto>> response = new HashMap<>();
        response.put("users", userDtos);
        return response;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OutputUserDto create(@Valid @RequestBody NewUserDto newUserDto) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User user = userService.save(newUserDto.getUsername(), newUserDto.getPassword());
        return new OutputUserDto(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(value = "id") Long id) {
        return userService.findById(id);
    }

//    @PutMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void update(@RequestBody User existingUser) {
//        userService.update(existingUser);
//    }

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

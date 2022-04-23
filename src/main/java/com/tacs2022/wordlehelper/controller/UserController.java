package com.tacs2022.wordlehelper.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.user.NewResultDto;
import com.tacs2022.wordlehelper.dtos.user.NewUserDto;
import com.tacs2022.wordlehelper.dtos.user.OutputUserDto;
import com.tacs2022.wordlehelper.dtos.user.OutputUsersDto;
import com.tacs2022.wordlehelper.service.UserService;

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

    @GetMapping()
    public OutputUsersDto getAllUsers() {
        List<User> users = userService.findAll();
        List<OutputUserDto> userDtos = users.stream().map(OutputUserDto::new).collect(Collectors.toList());
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
    public Result addUserResults(@Valid @RequestBody NewResultDto result, @PathVariable(value = "userId") Long userId){
        Result savedResult = result.fromDto();
        userService.addResult(userId, savedResult);
        return savedResult;
    }

    @GetMapping("{userId}/results")
    public List<Result> gi9ahuti(@PathVariable(value = "userId") Long userId){
        return userService.findById(userId).getResults();
    }

}

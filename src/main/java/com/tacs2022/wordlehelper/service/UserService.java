package com.tacs2022.wordlehelper.service;

import java.util.List;

import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    public List<User> findAll() {
        return (List<User>) userRepo.findAll();
    }

    public User findById(Long id) {
        return userRepo.findById(id).orElseThrow(
            () -> new NotFoundException("User with Id: " + id + " was not found")
        );
    }

}

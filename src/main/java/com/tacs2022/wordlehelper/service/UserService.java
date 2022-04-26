package com.tacs2022.wordlehelper.service;

import java.util.List;
import java.util.Optional;

import com.tacs2022.wordlehelper.controller.Exceptions.ExistingUserException;
import com.tacs2022.wordlehelper.domain.user.PasswordSecurity;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.repos.UserRepository;

import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User findByUsername(String username) {
        List<User> users = userRepo.findByUsername(username);
        return users.stream().findFirst().orElseThrow(
                () -> new NotFoundException("User with username: " + username + " was not found")
        );
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepo.findById(id).orElseThrow(
                () -> new NotFoundException("User with Id: " + id + " was not found")
        );
        userRepo.delete(user);
    }

    @Transactional
    public User save(String username, String password) {
        Optional<User> user = this.userRepo.findByUsername(username).stream().findFirst();

        if(user.isPresent()){
            throw new ExistingUserException();
        }

        PasswordSecurity passwordSecurity = this.getHashedSaltedPassword(password);

        if(passwordSecurity == null){
            System.out.println("Error getting salt and hash for password");
            return null;
        }

        User newUser = new User(username, passwordSecurity.getHashedSaltedPassword(), passwordSecurity.getSalt());
        userRepo.save(newUser);
        return newUser;
    }

    private PasswordSecurity getHashedSaltedPassword(String password){
        SecurityService hasher = new SecurityService();
        byte[] salt = hasher.getSalt();
        byte[] hashedSaltedPassword = hasher.hash(password, salt);

        if(salt == null || hashedSaltedPassword == null){
            return null;
        }

        return new PasswordSecurity(salt, hashedSaltedPassword);
    }

    @Transactional
    public void addResult(Long userId, Result result){
        User user = findById(userId);

        if(user.getResults().stream().anyMatch(result::match)){
            throw new ResultAlreadyLoadedException();
        }

            user.addResult(result);
    }

    /*public String getUsernameFromToken(String token){
        return TokenProvider.getUsername(token);
    }

    public Long getUserIdFromToken(String token){
        return TokenProvider.getId(token);
    }*/

}

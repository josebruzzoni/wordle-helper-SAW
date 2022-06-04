package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.user.PasswordSecurity;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.ExistingUserException;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.exceptions.ResultAlreadyLoadedException;
import com.tacs2022.wordlehelper.repos.UserRepository;
import com.tacs2022.wordlehelper.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    public List<User> findAll() {
        return (List<User>) userRepo.findAll();
    }

    public User findById(String id) {
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
    public void delete(String id) {
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
    public void addResult(String userId, Result result){
        User user = findById(userId);
        if(user.getResults().stream().anyMatch(result::matches)){
            throw new ResultAlreadyLoadedException();
        }

        user.addResult(result);
        userRepo.save(user);
    }
    
    public User getUserFromAuth(String auth) {
    	String token = auth.substring(7);
    	return this.getUserFromToken(token);
    }

    public User getUserFromToken(String token){
        String userId = TokenProvider.getId(token);
        return this.findById(userId);
    }

}

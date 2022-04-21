package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.controller.Exceptions.ExpiredRequestException;
import com.tacs2022.wordlehelper.domain.user.Session;
import com.tacs2022.wordlehelper.repos.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

@Service
public class SessionService {
    @Autowired
    SessionRepository sessionRepo;
    @Autowired
    UserService userService;
    @Autowired
    SecurityService securityService;

    public String getToken(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (securityService.validatePassword(username, password)){
            String token = UUID.randomUUID().toString();
            Session session = new Session(token, userService.findByUsername(username));
            this.sessionRepo.save(session);
            return token;
        }
        return null;
    }

    public void removeToken(String token) {
        Session session = this.sessionRepo.getByToken(token);
        if (session == null){
            //TODO crear una exception 404 para esto
            throw new ExpiredRequestException();
        }
        this.sessionRepo.delete(session);
    }
}

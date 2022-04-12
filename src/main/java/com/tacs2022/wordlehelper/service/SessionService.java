package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.Session;
import com.tacs2022.wordlehelper.dtos.AuthDto;
import com.tacs2022.wordlehelper.repos.SessionRepository;
import com.tacs2022.wordlehelper.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SessionService {
    @Autowired
    SessionRepository sessionRepo;
    @Autowired
    UserService userService;

    public String getToken(AuthDto authDto){
        String token = "token";
        Session session = new Session(1l, token, userService.findById(1l));
        session.token = token;
        this.sessionRepo.save(session);
        return token;
    }

    public void removeToken(String token) {
        Session session = this.sessionRepo.getByToken(token);
        this.sessionRepo.delete(session);
    }
}

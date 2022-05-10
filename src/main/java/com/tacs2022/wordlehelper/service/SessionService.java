package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.user.Session;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.ExpiredRequestException;
import com.tacs2022.wordlehelper.repos.SessionRepository;
import com.tacs2022.wordlehelper.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    @Autowired
    SessionRepository sessionRepo;
    @Autowired
    UserService userService;
    @Autowired
    SecurityService securityService;

    public String getToken(String username, String password) {
        if(securityService.validatePassword(username, password)){
            User user = userService.findByUsername(username);
            String token = TokenProvider.generateToken(user);
            Session session = new Session(token, user);
            this.sessionRepo.save(session);
            return token;
        }

        return null;
    }

    public void removeToken(String token) {
        Session session = this.sessionRepo.getByToken(token);

        if (session == null){
            throw new ExpiredRequestException();
        }
        this.sessionRepo.delete(session);
    }
}

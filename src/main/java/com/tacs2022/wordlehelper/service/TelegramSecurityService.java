package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.controller.Exceptions.ExpiredRequestException;
import com.tacs2022.wordlehelper.controller.Exceptions.InvalidUserException;
import com.tacs2022.wordlehelper.domain.user.Session;
import com.tacs2022.wordlehelper.domain.user.TelegramSession;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.repos.TelegramSessionRepository;
import com.tacs2022.wordlehelper.security.jwt.TokenProvider;
import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@Service
public class TelegramSecurityService {
    @Autowired
    TelegramSessionRepository telegramSessionRepository;
    @Autowired
    SecurityService securityService;
    @Autowired
    UserService userService;
    @Autowired
    SessionService sessionService;

    public void login(String username, String password, Long chatId) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String token = this.sessionService.getToken(username, password);
        TelegramSession telegramSession = new TelegramSession(chatId, token);
        this.telegramSessionRepository.save(telegramSession);
    }

    public void logout(Long chatId) {
        Optional<TelegramSession> telegramSession = this.telegramSessionRepository.findById(chatId);

        if(telegramSession.isEmpty()){
            throw new NotFoundException("Chat not found");
        }

        this.sessionService.removeToken(telegramSession.get().getToken());
    }
}

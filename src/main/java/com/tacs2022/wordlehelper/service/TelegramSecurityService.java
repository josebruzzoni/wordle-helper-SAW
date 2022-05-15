package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.user.TelegramSession;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.repos.TelegramSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public User login(String username, String password, Long chatId) {
        String token = this.sessionService.getToken(username, password);

        if(token == null){
            return null;
        }

        TelegramSession telegramSession = new TelegramSession(chatId, token);
        this.telegramSessionRepository.save(telegramSession);

        return this.userService.findByUsername(username);
    }

    public void logout(Long chatId) {
        Optional<TelegramSession> telegramSession = this.telegramSessionRepository.findById(chatId);

        if(telegramSession.isEmpty()){
            throw new NotFoundException("Chat not found");
        }

        this.sessionService.removeToken(telegramSession.get().getToken());
    }

    public User getUserFromToken(Long chatId){
        Optional<TelegramSession> telegramSession = this.telegramSessionRepository.findById(chatId);

        if(telegramSession.isEmpty()){
            return null;
        }

        String token = telegramSession.get().getToken();
        System.out.printf("token: %s\n", token);
        return userService.getUserFromToken(token);
    }
}

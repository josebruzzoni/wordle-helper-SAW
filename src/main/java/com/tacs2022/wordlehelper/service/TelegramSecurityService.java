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
        Optional<TelegramSession> possibleTelegramSession = this.telegramSessionRepository.findById(chatId);

        if(possibleTelegramSession.isEmpty()){
            throw new NotFoundException("Chat not found");
        }

        TelegramSession telegramSession = possibleTelegramSession.get();

        this.sessionService.removeToken(telegramSession.getToken());
        this.telegramSessionRepository.delete(telegramSession);
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

    public boolean isUserLogged(Long chatId){
        return this.telegramSessionRepository.existsById(chatId);
    }
}

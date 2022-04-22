package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.user.Session;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.security.jwt.TokenProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
public class TelegramTempService {
    // Provisorio hasta solucionar la conexión con el UserService y SessionService
    private Map<Long, String> sessionByChatId = new HashMap<>();
    private ArrayList<User> users = new ArrayList();

    public String getToken(long chatId, String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Optional<User> possibleUser = this.users.stream().filter(user1 -> Objects.equals(user1.getUsername(), username)).findFirst();

        if(!possibleUser.isPresent()){
            return null;
        }

        User user = possibleUser.get();

        if(!new SecurityService().validatePassword(user, password)){
            return null;
        }

        String token = TokenProvider.generateToken(user);
        this.sessionByChatId.put(chatId, token);
        return token;
    }

    public User save(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecurityService hasher = new SecurityService();
        byte[] salt = hasher.getSalt();
        byte[] hashedSaltedPassword = hasher.hash(password, salt);
        User newUser = new User(username, hashedSaltedPassword, salt);
        this.users.add(newUser);
        return newUser;
    }

}

package com.tacs2022.wordlehelper.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("telegramSessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramSession {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    private String chatId;
    private String token;
}

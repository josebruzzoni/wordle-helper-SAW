package com.tacs2022.wordlehelper.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("telegramsessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramSession {
    @Id
    private String chatId;
    private String token;
}

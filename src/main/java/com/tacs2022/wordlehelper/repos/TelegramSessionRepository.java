package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.user.TelegramSession;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TelegramSessionRepository extends MongoRepository<TelegramSession, String> {
}

package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.user.Session;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<Session, String> {
    public Session getByToken(String token);
}

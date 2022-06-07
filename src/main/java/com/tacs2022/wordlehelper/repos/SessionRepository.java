package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.user.Session;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SessionRepository extends MongoRepository<Session, String> {
    public Session getByToken(String token);

    @Query(value="{ 'user.username': ?0 }")
    public Session findByUsername(String username);
}

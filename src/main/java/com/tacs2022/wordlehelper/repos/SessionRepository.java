package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.user.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, Long> {
    public Session getByToken(String token);
}

package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.user.TelegramSession;
import org.springframework.data.repository.CrudRepository;

public interface TelegramSessionRepository extends CrudRepository<TelegramSession, Long> {
}

package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    
}

package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.user.User;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
public interface UserRepository extends MongoRepository<User, String> {
    public List<User> findByUsername(String username);
}

package com.tacs2022.wordlehelper.repos;

import com.tacs2022.wordlehelper.domain.user.User;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
public interface UserRepository extends CrudRepository<User, Long> {
    public List<User> findByUsername(String username);
}

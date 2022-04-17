package com.tacs2022.wordlehelper.domain.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue
    private Long id;
    private String username;
    private byte[] hashedPass;
    private byte[] salt;

    public User(String username, byte[] hashedPass, byte[] salt) {
        this.username = username;
        this.hashedPass = hashedPass;
        this.salt = salt;
    }

    @OneToMany
    private List<Result> results;
}

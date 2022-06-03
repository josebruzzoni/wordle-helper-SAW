package com.tacs2022.wordlehelper.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Document("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String username;
    @JsonIgnore
    private byte[] hashedPass;
    @JsonIgnore
    private byte[] salt;
    private List<Result> results = new LinkedList<>();


    public User(String username, byte[] hashedPass, byte[] salt) {
        this.username = username;
        this.hashedPass = hashedPass;
        this.salt = salt;
    }

    public void addResult(Result result) {
        this.results.add(result);
    }
}

package com.tacs2022.wordlehelper.domain.user;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import java.util.LinkedList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue
    private Long id;
    private String username;
    @JsonIgnore
    private byte[] hashedPass;
    @JsonIgnore
    private byte[] salt;
    @OneToMany @Cascade(CascadeType.ALL)
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

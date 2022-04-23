package com.tacs2022.wordlehelper.domain.user;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id @GeneratedValue
    public Long id;
    public String token;
    @ManyToOne
    public User user;

    public Session(String token, User user){
        this.token = token;
        this.user = user;
    }

}


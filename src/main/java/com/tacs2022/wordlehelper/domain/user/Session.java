package com.tacs2022.wordlehelper.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    public String id;
    public String token;
    @ManyToOne
    public User user;

    public Session(String token, User user){
        this.token = token;
        this.user = user;
    }

}


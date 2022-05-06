package com.tacs2022.wordlehelper.dtos.user;

import com.tacs2022.wordlehelper.domain.user.User;
import lombok.Data;

@Data
public class UsernameDto {
    private Long id;
    private String username;

    public UsernameDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
    }
}

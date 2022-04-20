package com.tacs2022.wordlehelper.dtos.user;

import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OutputUserDto {
    private Long id;
    private String username;
    private List<Result> results;

    public OutputUserDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.results = user.getResults();
    }
}


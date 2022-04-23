package com.tacs2022.wordlehelper.dtos.user;

import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OutputUsersDto {
    private List<OutputUserDto> users;
}

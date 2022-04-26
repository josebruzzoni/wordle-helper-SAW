package com.tacs2022.wordlehelper.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OutputUsersDto {
    private List<OutputUserDto> users;
}

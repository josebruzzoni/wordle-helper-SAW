package com.tacs2022.wordlehelper.dtos.user;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NewUserDto {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;
}

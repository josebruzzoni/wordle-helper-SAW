package com.tacs2022.wordlehelper.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class PasswordSecurity {
    @Getter @Setter
    private byte[] salt;
    @Getter @Setter
    private byte[] hashedSaltedPassword;
}

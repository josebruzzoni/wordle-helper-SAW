package com.tacs2022.wordlehelper.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Command {
    private String name;
    private boolean needsAuthorization;
}

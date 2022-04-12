package com.tacs2022.wordlehelper.domain;

import lombok.Data;

@Data
public class LetterPlay {

    private Character letter;
    private Integer position;
    private LetterColor color;
}


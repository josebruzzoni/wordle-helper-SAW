package com.tacs2022.wordlehelper.domain.play;

import lombok.Data;

@Data
public class LetterPlay {

    private Character letter;
    private Integer position;
    private LetterColor color;
}


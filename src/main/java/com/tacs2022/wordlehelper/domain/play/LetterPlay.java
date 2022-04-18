package com.tacs2022.wordlehelper.domain.play;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LetterPlay {

    private Character letter;
    private Integer position;
    private LetterColor color;
}


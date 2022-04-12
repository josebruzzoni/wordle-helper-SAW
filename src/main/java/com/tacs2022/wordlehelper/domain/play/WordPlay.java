package com.tacs2022.wordlehelper.domain.play;

import lombok.Data;

import java.util.List;

@Data
public class WordPlay {
    private List<LetterPlay> letters;
}

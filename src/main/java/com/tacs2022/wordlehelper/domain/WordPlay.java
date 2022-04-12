package com.tacs2022.wordlehelper.domain;

import lombok.Data;

import java.util.List;

@Data
public class WordPlay {
    private List<LetterPlay> letters;
}

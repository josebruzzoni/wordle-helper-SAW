package com.tacs2022.wordlehelper.domain.play;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WordPlay {
    private List<LetterPlay> letters;

    public WordPlay(String greyLettersPlayed, String yellowLettersPlayed, String greenLettersPlayed){
        letters = new ArrayList<>();
        //process grey letters
        for (Character c : greyLettersPlayed.toCharArray()){
            letters.add(new LetterPlay(c, -1, LetterColor.GRAY));
        }
        //process yellow letters
        for (int i = 0 ; i < yellowLettersPlayed.length() ; i++){
            letters.add(new LetterPlay(yellowLettersPlayed.charAt(i), i, LetterColor.YELLOW));
        }
        //process green letters
        for (int i = 0 ; i < greenLettersPlayed.length() ; i++){
            letters.add(new LetterPlay(greenLettersPlayed.charAt(i), i, LetterColor.GREEN));
        }
    }
}

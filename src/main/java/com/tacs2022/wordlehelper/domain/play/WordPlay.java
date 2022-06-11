package com.tacs2022.wordlehelper.domain.play;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class WordPlay {
    private static final Character BLANK_INDICATOR = '_';

    private List<LetterPlay> letters;

    public WordPlay(String grayLettersPlayed, String yellowLettersPlayed, String greenLettersPlayed){
        letters = new ArrayList<>();
        //process grey letters
        readGrayLettersFromString(grayLettersPlayed);
        //process yellow letters
        readYellowLettersFromString(yellowLettersPlayed);
        //process green letters
        readGreenLettersFromString(greenLettersPlayed);

        //TODO: throw exception if empty?
    }

    private void readGrayLettersFromString(String string){
        if (string == null) return;

        for (int i = 0 ; i < string.length() ; i++){
            Character c = string.toLowerCase().charAt(i);
            letters.add(new LetterPlay(c, -1, LetterColor.GRAY));
        }
    }

    private void readYellowLettersFromString(String string){
        if (string == null) return;

        for (int i = 0 ; i < string.length() ; i+=2){
            Character index = string.toLowerCase().charAt(i);
            Character c = string.toLowerCase().charAt(i+1);
            letters.add(new LetterPlay(c, Integer.valueOf(index.toString()), LetterColor.YELLOW));
        }
    }

    private void readGreenLettersFromString(String string){
        if (string == null) return;

        for (int i = 0 ; i < string.length() ; i++){
            Character c = string.toLowerCase().charAt(i);
            if (!c.equals(BLANK_INDICATOR)) letters.add(new LetterPlay(c, i, LetterColor.GREEN));
        }
    }

    public List<LetterPlay> getLettersByColor(LetterColor color){
        return letters.stream().filter(l -> l.getColor().equals(color)).collect(Collectors.toList());
    }
}

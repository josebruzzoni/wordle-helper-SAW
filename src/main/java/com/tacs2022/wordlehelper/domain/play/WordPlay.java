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
        readLettersFromStringNoPosition(grayLettersPlayed);
        //process yellow letters
        readLettersFromStringWithPositions(yellowLettersPlayed, LetterColor.YELLOW);
        //process green letters
        readLettersFromStringWithPositions(greenLettersPlayed, LetterColor.GREEN);

        //TODO: throw exception if empty?
    }

    private void readLettersFromStringNoPosition(String string){
        if (string == null) return;

        for (int i = 0 ; i < string.length() ; i++){
            Character c = string.toLowerCase().charAt(i);
            letters.add(new LetterPlay(c, -1, LetterColor.GRAY));
        }
    }

    private void readLettersFromStringWithPositions(String string, LetterColor targetColor){
        if (string == null) return;

        for (int i = 0 ; i < string.length() ; i++){
            Character c = string.toLowerCase().charAt(i);
            if (!c.equals(BLANK_INDICATOR)) letters.add(new LetterPlay(c, i, targetColor));
        }
    }

    public List<LetterPlay> getLettersByColor(LetterColor color){
        return letters.stream().filter(l -> l.getColor().equals(color)).collect(Collectors.toList());
    }
}

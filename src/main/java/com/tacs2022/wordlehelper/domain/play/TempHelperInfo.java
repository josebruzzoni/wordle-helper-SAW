package com.tacs2022.wordlehelper.domain.play;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.exceptions.InvalidPlayException;
import com.tacs2022.wordlehelper.exceptions.LetterMismatchException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

@Data
@NoArgsConstructor
public class TempHelperInfo {
    private String grayLettersPlayed = "";
    private String yellowLettersPlayed = "";
    private String greenLettersPlayed = "_____";
    private Language language;

    public void addGreyLetterPlayed(char letter){
        if(!this.grayLettersPlayed.contains(String.valueOf(letter))){
            this.grayLettersPlayed += letter;
        }
    }

    public void addYellowLetterPlayed(int position, char letter){
        this.yellowLettersPlayed += String.format("%d%c", position, letter);
    }

    public void addGreenLetterPlayed(int position, char letter){
        char charAtPosition = this.greenLettersPlayed.charAt(position);

        if(charAtPosition != '_' && charAtPosition != letter){
            throw new LetterMismatchException(position, letter);
        }

        if(charAtPosition == letter){
            return;
        }

        String leftSideOfPosition = this.greenLettersPlayed.substring(0, position);
        String rightSideOfPosition = "";
        int lastPosition = this.greenLettersPlayed.length()-1;

        if(position != lastPosition){
            rightSideOfPosition = this.greenLettersPlayed.substring(position+1);
        }

        this.greenLettersPlayed = leftSideOfPosition + letter + rightSideOfPosition;
    }
}

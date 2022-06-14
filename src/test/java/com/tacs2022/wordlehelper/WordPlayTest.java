package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.play.LetterColor;
import com.tacs2022.wordlehelper.domain.play.LetterPlay;
import com.tacs2022.wordlehelper.domain.play.WordPlay;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class WordPlayTest {

    @Test
    void test_Constructor(){
        WordPlay wordPlay = new WordPlay("AL", "1I3C", "____O");

        List<LetterPlay> expectedLetters = List.of(
                new LetterPlay('a', -1, LetterColor.GRAY),
                new LetterPlay('l', -1, LetterColor.GRAY),
                new LetterPlay('i', 1, LetterColor.YELLOW),
                new LetterPlay('c', 3, LetterColor.YELLOW),
                new LetterPlay('o', 4, LetterColor.GREEN)
        );

        assertTrue(wordPlay.getLetters().containsAll(expectedLetters) && expectedLetters.containsAll(wordPlay.getLetters()));
    }

    @Test
    void test_ConstructorNullArguments(){
        WordPlay wordPlay = new WordPlay(null, null, null);
        assertTrue(wordPlay.getLetters().isEmpty());
    }

    @Test
    void test_ConstructorEmptyArguments(){
        WordPlay wordPlay = new WordPlay("", "", "");
        assertTrue(wordPlay.getLetters().isEmpty());
    }
}

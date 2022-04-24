package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.play.LetterColor;
import com.tacs2022.wordlehelper.domain.play.WordPlay;
import com.tacs2022.wordlehelper.service.HelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HelperServiceTest {
    HelperService helperService;
    WordPlay wordPlay, emptyPlay;

    @BeforeEach
    void init(){
        helperService = new HelperService();
        wordPlay = new WordPlay("KIR", "G__W_", "A_L__");
        emptyPlay = new WordPlay(null, null, null);
    }

    //TODO: standardize test names

    @Test
    void whenGivenPlay_HelperMustBuildRegularExpression_AllInPosition(){
        String regexFromGreen = helperService.buildAllInPositionRegularExpression(wordPlay.getLettersByColor(LetterColor.GREEN)).toUpperCase();
        assertEquals("A.L..", regexFromGreen);
    }

    @Test
    void whenGivenEmptyGreenPlay_HelperMustBuildRegularExpression_Any(){
        String regexFromGreen = helperService.buildAllInPositionRegularExpression(emptyPlay.getLettersByColor(LetterColor.GREEN)).toUpperCase();
        assertEquals(".....", regexFromGreen);
    }

    @Test
    void whenGivenPlay_HelperMustBuildRegularExpression_NoneOf(){
        String regexFromGray = helperService.buildNoneOfRegularExpression(wordPlay.getLettersByColor(LetterColor.GRAY)).toUpperCase();
        assertEquals("[^KIR][^KIR][^KIR][^KIR][^KIR]", regexFromGray);
    }

    @Test
    void whenGivenEmptyGrayPlay_HelperMustBuildRegularExpression_Any(){
        String regexFromGray = helperService.buildNoneOfRegularExpression(emptyPlay.getLettersByColor(LetterColor.GRAY)).toUpperCase();
        assertEquals(".....", regexFromGray);
    }

    @ParameterizedTest
    @CsvSource({
            "AGLOW, true", //contains both and not in position
            "GAMES, false", //contains at least one in position
            "WATER, false", //contains only one not in position
            "PIZZA, false" //contains none
    })
    void test_hasEveryOf_NotInPosition(String word, boolean matches){
        assertEquals(matches, helperService.hasEveryOfNotInPosition(word.toLowerCase(), wordPlay.getLettersByColor(LetterColor.YELLOW)));
    }

    @Test
    void whenGivenEmptyYellowPlay_hasEveryOfNotInPosition_mustMatch(){
        assertTrue(helperService.hasEveryOfNotInPosition("MATCH", emptyPlay.getLettersByColor(LetterColor.YELLOW)));
    }

    //TODO: more tests
    @ParameterizedTest
    @MethodSource
    void test_getWordsByPlay(WordPlay wordPlay, Language language, List<String> expected){
        List<String> possibleWords = helperService.getWordsByPlay(wordPlay, language);
        assertTrue(expected.containsAll(possibleWords) && possibleWords.containsAll(expected));
    }

    private static Stream<Arguments> test_getWordsByPlay() {
        return Stream.of(
                Arguments.of(new WordPlay("KIR", "W____", "A_L__"), Language.EN, List.of("ABLOW", "AFLOW", "ALLOW", "AGLOW")),
                Arguments.of(new WordPlay("ARBLFUEGPS", "_I_C_", "____O"), Language.ES, List.of("CHINO", "CHITO", "CHIVO", "COIDO", "COITO", "ICONO", "TOCIO"))
        );
    }

}

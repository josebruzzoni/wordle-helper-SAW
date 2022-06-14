package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.play.WordPlay;
import com.tacs2022.wordlehelper.service.HelperService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HelperServiceTest {

    private HelperService helperService;

    @BeforeEach
    void init(){
        helperService = new HelperService();
    }

    @ParameterizedTest
    @MethodSource
    void test_getWordsByPlay(WordPlay wordPlay, Language language, List<String> expected){
        List<String> possibleWords = helperService.getWordsByPlay(wordPlay, language);
        assertTrue(expected.containsAll(possibleWords) && possibleWords.containsAll(expected));
    }

    private static Stream<Arguments> test_getWordsByPlay() {
        return Stream.of(
                Arguments.of(new WordPlay("KIR", "0W", "A_L__"), Language.EN, List.of("ABLOW", "AFLOW", "ALLOW", "AGLOW")),
                Arguments.of(new WordPlay("ARBLFUEGPS", "1I3C", "____O"), Language.ES, List.of("CHINO", "CHITO", "CHIVO", "COIDO", "COITO", "ICONO", "TOCIO"))
        );
    }

    @Test
    void test_emptyPlay_returnsAllWords(){
        int spanishListCount = 3898;
        WordPlay emptyWordPlay = new WordPlay("", "", "");
        List<String> possibleWords = helperService.getWordsByPlay(emptyWordPlay, Language.ES);
        assertEquals(spanishListCount, possibleWords.size());
    }
}

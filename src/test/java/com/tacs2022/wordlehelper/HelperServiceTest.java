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

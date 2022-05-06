package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.play.WordPlay;
import com.tacs2022.wordlehelper.dtos.helper.OutputPossibleWordsDto;
import com.tacs2022.wordlehelper.exceptions.InvalidPlayException;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.exceptions.NullParametersException;
import com.tacs2022.wordlehelper.service.HelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static com.tacs2022.wordlehelper.service.HelperService.WORD_LENGTH;

@RestController
@RequestMapping("/v1/helper")
public class HelperController {
    @Autowired
    private HelperService helperService;

    /**
     * Generates a list of possible answers for the given combination of letters played based on
     * the rules of the game.
     *
     * - Grey letters are those that are not contained in the winning word.
     * - Yellow letters are those that are contained in the word, but not in the correct position they were played.
     * - Green letters are contained in the answer in the exact position they were played.
     *
     * @param lan Language the game was played in.
     * @param greyLettersPlayed Concatenated letters played that resulted colored grey (i.e.: "ARBLFUEGPS")
     * @param yellowLettersPlayed A string of 5 characters that represent the letters and respective positions
     *                            they were played of those that resulted colored yellow, using '_' where no
     *                            yellow letter was played (i.e.: "_I_C_")
     * @param greenLettersPlayed A string of 5 characters that represent the letters and respective positions
     *                           they were played of those that resulted colored green, using '_' where no
     *                           green letter was played (i.e.: "T__O_")
     * @return A list of possible words that could be the winning word based on the provided play
     */
    @GetMapping("/words")
    public OutputPossibleWordsDto getPossibleWords(@RequestParam(value = "language") String lan, @RequestParam(value = "grey") String greyLettersPlayed,
                                                      @RequestParam(value = "yellow") String yellowLettersPlayed,
                                                      @RequestParam(value = "green") String greenLettersPlayed) {

        validateParams(greyLettersPlayed, yellowLettersPlayed, greenLettersPlayed);
        Language language = validateLanguage(lan);

        WordPlay attemptedPlay = new WordPlay(greyLettersPlayed, yellowLettersPlayed, greenLettersPlayed);

        List<String> possibleWords = helperService.getWordsByPlay(attemptedPlay, language);
        return new OutputPossibleWordsDto(possibleWords);
    }




    private void validateParams(String gray, String yellow, String green){
        List<String> params = List.of(gray, yellow, green);
        // Validate all null params. At least one of the params must be not null.
        if (params.stream().allMatch(Objects::isNull)) throw new NullParametersException("At least one parameter must be not null");

        // Validate that count of all characters sent is at least 5
        if (params.stream().filter(Objects::nonNull)
                .map(s -> s.replace("_", ""))
                .reduce((acc, e) -> acc  + e)
                .filter(s -> s.length() >= WORD_LENGTH).isEmpty())
            throw new InvalidPlayException("A Wordle play must be of at least " + WORD_LENGTH + " characters combined");

        // Validate correct format of params => yellow and green must be five characters long, with only letters and '_'
        validatePositionPlay(yellow);
        validatePositionPlay(green);
    }

    private void validatePositionPlay(String play){
        if (play == null || play.isEmpty())
            return;
        if (!play.matches(String.format("[a-zA-Z_]{%s}", WORD_LENGTH)))
            throw new InvalidPlayException("Yellow and Green parameters must be five characters long and contain only letters or underscore");
    }

    private Language validateLanguage(String language){
        try {
            return Language.valueOf(language.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new NotFoundException("Language not found");
        }catch (NullPointerException e){
            throw new NullParametersException("Language parameter must be not null");
        }
    }
}
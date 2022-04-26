package com.tacs2022.wordlehelper.controller;


import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;
import com.tacs2022.wordlehelper.service.DictionaryService;

import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    @Autowired
    DictionaryService dictionaryService;

    @GetMapping("/{language}/words/{word}")
    public Word getWord(@PathVariable(value = "language") String lan, @PathVariable(value = "word") String wordName) {
        Language language = validateLanguage(lan);
        return dictionaryService.findByNameAndLanguage(wordName, language);
    }

    private Language validateLanguage(String language){
        try {
            return Language.valueOf(language.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new NotFoundException("Language not found");
        }
    }
}

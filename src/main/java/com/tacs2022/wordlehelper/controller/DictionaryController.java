package com.tacs2022.wordlehelper.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tacs2022.wordlehelper.domain.dictionary.Word;
import com.tacs2022.wordlehelper.service.DictionaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    @Autowired
    DictionaryService dictionaryService;

    @GetMapping("/{language}/words/{word}")
    public Word getWord(@PathVariable(value = "language") String language, @PathVariable(value = "word") String wordName) {
        return dictionaryService.findByNameAndLanguage(wordName, language);
    }
}

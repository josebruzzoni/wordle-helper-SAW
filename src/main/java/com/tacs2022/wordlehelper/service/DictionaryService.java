package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;

import com.tacs2022.wordlehelper.service.dictionaryapis.DictionaryAPI;
import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictionaryService {

    @Autowired
    DictionaryAPI dictionaryAPI;

    public Word findByNameAndLanguage(String name, String language) {
        Language l;

        try {
            l = Language.valueOf(language.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new NotFoundException("Language not found");
        }

        return dictionaryAPI.getWordDefinition(name, l);
    }
}
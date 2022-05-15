package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;
import com.tacs2022.wordlehelper.service.dictionaryapis.DictionaryAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DictionaryService {

    @Autowired
    DictionaryAPI dictionaryAPI;

    @Cacheable("dictionary-words")
    public Word findByNameAndLanguage(String name, Language language) {
        return dictionaryAPI.getWordDefinition(name, language);
    }
}
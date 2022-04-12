package com.tacs2022.wordlehelper.service;

import java.util.List;

import com.tacs2022.wordlehelper.domain.dictionary.Word;

import org.springframework.stereotype.Service;

@Service
public class DictionaryService {
    public Word findByNameAndLanguage(String name, String language) {
        return new Word("1", name, "definition", language);
    }

    public List<Word> findAllByNameAndLanguage(String name, String language) {
        return List.of(new Word("1", name, "definition", language));
    }
}

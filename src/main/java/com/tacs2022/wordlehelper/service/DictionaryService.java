package com.tacs2022.wordlehelper.service;

import java.util.ArrayList;
import java.util.List;

import com.tacs2022.wordlehelper.domain.Word;

import org.springframework.stereotype.Service;

@Service
public class DictionaryService {
    public Word findByName(String name, String language) {
        return new Word("1", name, "definition", language);
    }

    public List<Word> findAllByName(String name, String language) {
        List<Word> words = new ArrayList<Word>();
        words.add(new Word("1", name, "definition", language));
        return words;
    }
}

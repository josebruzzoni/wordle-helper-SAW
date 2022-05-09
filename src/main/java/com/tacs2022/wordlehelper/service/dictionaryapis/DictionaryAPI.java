package com.tacs2022.wordlehelper.service.dictionaryapis;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;

public interface DictionaryAPI {
    public Word getWordDefinition(String word, Language language);
}

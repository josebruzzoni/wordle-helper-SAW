package com.tacs2022.wordlehelper.domain.dictionary;

import com.tacs2022.wordlehelper.domain.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {

    private String name;
    private String definition;
    private Language language;
}

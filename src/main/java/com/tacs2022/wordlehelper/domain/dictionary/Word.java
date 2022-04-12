package com.tacs2022.wordlehelper.domain.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Word {
    private String id;
    private String name;
    private String definition;
    private String language;
}

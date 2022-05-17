package com.tacs2022.wordlehelper.utils;

import com.tacs2022.wordlehelper.domain.Language;

import java.util.List;
import java.util.stream.Collectors;

public class LanguageUtils {
    public static String format(List<Language> languages){
        return languages.stream().map(language -> StringUtils.capitalize(language.getLanguage())).collect(Collectors.joining(", "));
    }
}

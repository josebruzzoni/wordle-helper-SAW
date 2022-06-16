package com.tacs2022.wordlehelper.domain;

public enum Language {
    EN("English"),
    ES("Spanish");

    private final String language;

    Language(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }
}

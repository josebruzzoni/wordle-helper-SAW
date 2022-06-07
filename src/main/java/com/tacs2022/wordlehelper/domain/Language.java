package com.tacs2022.wordlehelper.domain;

public enum Language {
    EN("english"),
    ES("spanish");

    private final String language;

    Language(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }
}

package com.tacs2022.wordlehelper.domain.tournaments;

import com.tacs2022.wordlehelper.utils.StringUtils;

public enum Visibility {
    PRIVATE("private"),
    PUBLIC("public");

    private final String visibility;

    Visibility(String visibility) {
        this.visibility = visibility;
    }

    public String getCapitalized() {
        return StringUtils.capitalize(this.visibility);
    }
}

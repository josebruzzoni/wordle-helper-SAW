package com.tacs2022.wordlehelper.controller.exceptions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MissingAttributesException extends RuntimeException {
    private List<String>missingAttributes;

    public MissingAttributesException(String ... missingAttributes){
        this.missingAttributes = Arrays.stream(missingAttributes).collect(Collectors.toList());
    }

    public List<String> getMissingAttributes() {
        return missingAttributes;
    }
}

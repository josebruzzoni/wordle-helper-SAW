package com.tacs2022.wordlehelper.controller.Exceptions;

import java.util.Arrays;
import java.util.LinkedList;
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

package com.tacs2022.wordlehelper.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingAttributesException.class)
    public Map<String, List<String>> handleMissingAttributes(MissingAttributesException e){
        Map<String, List<String>> missingAttributes = new HashMap<>();
        missingAttributes.put("missingAttributes", e.getMissingAttributes());
        return missingAttributes;
    }
}

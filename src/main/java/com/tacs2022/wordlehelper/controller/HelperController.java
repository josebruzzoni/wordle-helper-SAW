package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.User;
import com.tacs2022.wordlehelper.domain.WordPlay;
import com.tacs2022.wordlehelper.service.HelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/helper")
public class HelperController {
    @Autowired
    private HelperService helperService;

    @GetMapping("/words")
    public List<String> getPossibleWords(@RequestBody WordPlay attemptedPlay) {
        return List.of("ALLOW", "AGLOW");
    }
}

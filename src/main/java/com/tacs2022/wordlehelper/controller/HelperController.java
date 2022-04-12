package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.play.WordPlay;
import com.tacs2022.wordlehelper.service.HelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/helper")
public class HelperController {
    @Autowired
    private HelperService helperService;

    @GetMapping("/words")
    public Map<String, List<String>> getPossibleWords(@RequestBody WordPlay attemptedPlay) {
        List<String> possibleWords = helperService.getWordsByPlay(attemptedPlay);
        Map<String, List<String>> response = new HashMap<>();
        response.put("possibleWords", possibleWords);
        return response;
    }
}

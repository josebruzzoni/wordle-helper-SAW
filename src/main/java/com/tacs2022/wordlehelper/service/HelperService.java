package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.play.WordPlay;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelperService {

    public List<String> getWordsByPlay(WordPlay wordPlay){
        //TODO
        return List.of("ALLOW", "AGLOW");
    }
}

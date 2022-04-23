package com.tacs2022.wordlehelper.dtos.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OutputPossibleWordsDto {
    List<String> possibleWords;
}

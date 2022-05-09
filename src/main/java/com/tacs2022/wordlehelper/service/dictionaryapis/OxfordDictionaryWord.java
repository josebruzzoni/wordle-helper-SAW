package com.tacs2022.wordlehelper.service.dictionaryapis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OxfordDictionaryWord {
    @JsonProperty("id")
    private String id;
    private String definition;
    private String language;

    @JsonProperty("results")
    private void unpackDefinitionAndLanguage(List<Map<String, Object>> results){
        List<Map<String, Object>> lexicalEntries = (List<Map<String, Object>>) results.get(0).get("lexicalEntries");
        List<Map<String, Object>> entries = (List<Map<String, Object>>) lexicalEntries.get(0).get("entries");
        List<Map<String, Object>> senses = (List<Map<String, Object>>) entries.get(0).get("senses");
        List<String> definitions = (List<String>) senses.get(0).get("definitions");

        this.definition = definitions.get(0);
        this.language = (String) results.get(0).get("language");
    }

    public Word asWord(){
        Language lan = Language.valueOf(language.substring(0, 2).toUpperCase()); //get first to letters of language locale ==> 'en_US' to 'EN'
        return new Word(id, definition, lan);
    }
}

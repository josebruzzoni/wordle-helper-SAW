package com.tacs2022.wordlehelper.domain.dictionary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Word {

    private String name;
    private String definition;
    private String language;

    @JsonProperty("id")
    private void unpackName(String id){
        this.name = id;
    }

    @JsonProperty("results")
    private void unpackDefinitionAndLanguage(List<Map<String, Object>> results){
        List<Map<String, Object>> lexicalEntries = (List<Map<String, Object>>) results.get(0).get("lexicalEntries");
        List<Map<String, Object>> entries = (List<Map<String, Object>>) lexicalEntries.get(0).get("entries");
        List<Map<String, Object>> senses = (List<Map<String, Object>>) entries.get(0).get("senses");
        List<String> definitions = (List<String>) senses.get(0).get("definitions");

        this.definition = definitions.get(0);
        this.language = (String) results.get(0).get("language");
    }

    //TODO: separate deserialization logic from data object
}

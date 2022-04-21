package com.tacs2022.wordlehelper.service.dictionaryapis;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OxfordDictionaryAPI implements DictionaryAPI{

    private static final String API_URL = "https://od-api.oxforddictionaries.com:443/api/v2/entries/%s/%s";

    @Override
    public Word getWordDefinition(String word, Language language) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        //TODO: remove api keys from code
        headers.add("app_id", "bcaeea81");
        headers.add("app_key", "47552003a1bea91d83e7cac33e408554");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Word> response = restTemplate.exchange(String.format(API_URL, language.toString().toLowerCase(), word), HttpMethod.GET, requestEntity, Word.class);
        return response.getBody();
    }
}

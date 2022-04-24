package com.tacs2022.wordlehelper.service.dictionaryapis;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;
import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OxfordDictionaryAPI implements DictionaryAPI{

    private static final String API_URL = "https://od-api.oxforddictionaries.com:443/api/v2/entries/%s/%s";
    //TODO: remove api keys from code
    private static final String APP_ID = "bcaeea81";
    private static final String APP_KEY = "47552003a1bea91d83e7cac33e408554";

    @Override
    public Word getWordDefinition(String word, Language language) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("app_id", APP_ID);
        headers.add("app_key", APP_KEY);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Word> response = restTemplate.exchange(String.format(API_URL, language.toString().toLowerCase(), word), HttpMethod.GET, requestEntity, Word.class);
            return response.getBody();

        }catch (HttpClientErrorException e){
            throw new NotFoundException(String.format("No dictionary entry found for language: %s and word: %s", language, word));
        }
    }
}

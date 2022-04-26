package com.tacs2022.wordlehelper.service.dictionaryapis;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;
import com.tacs2022.wordlehelper.service.exceptions.NotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
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

    private static final String APP_ID_ENV = "OXFORD_DICTIONARY_API_ID";
    private static final String APP_KEY_ENV = "OXFORD_DICTIONARY_API_KEY";

    @Override
    public Word getWordDefinition(String word, Language language) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        Dotenv dotenv = Dotenv.load();
        headers.add("app_id", dotenv.get(APP_ID_ENV));
        headers.add("app_key", dotenv.get(APP_KEY_ENV));

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<OxfordDictionaryWord> response = restTemplate.exchange(String.format(API_URL, language.toString().toLowerCase(), word), HttpMethod.GET, requestEntity, OxfordDictionaryWord.class);
            return response.getBody().asWord();
        }catch (HttpClientErrorException e){
            throw new NotFoundException(String.format("No dictionary entry found for language: %s and word: %s", language, word));
        }
    }
}

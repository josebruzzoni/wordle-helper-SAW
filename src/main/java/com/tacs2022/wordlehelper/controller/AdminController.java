package com.tacs2022.wordlehelper.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String admin(@RequestBody String body) {
        jdbcTemplate.execute("SELECT * FROM table WHERE algo=" + body);
        return body;
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String getAlgo() {
        jdbcTemplate.execute("SELECT * FROM table WHERE algo=");
        return "";
    }
}

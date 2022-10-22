package com.tacs2022.wordlehelper.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public String admin(@RequestBody String body) {
        System.out.println(body);
        return body;
    }
}

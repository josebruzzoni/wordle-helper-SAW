package com.tacs2022.wordlehelper.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void admin(@RequestBody String body) throws UnsupportedEncodingException {
        String newBody = URLDecoder.decode(body, StandardCharsets.UTF_8.toString());
        System.out.println("INSERT INTO participants (name,score) VALUES (\'"+newBody+"\',"+100+");");
        jdbcTemplate.execute("INSERT INTO participants (name,score) VALUES (\'"+newBody+"\',"+100+");");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Participant> get() {
        return jdbcTemplate.query("SELECT * FROM participants",new RowMapper<Participant>(){
            @Override
            public Participant mapRow(ResultSet rs, int rownumber) throws  SQLException    {
                Participant e=new Participant();
                e.setId(rs.getInt(1));
                e.setName(rs.getString(2));
                e.setScore(rs.getInt(3));
                return e;
            }
        });
    }
}

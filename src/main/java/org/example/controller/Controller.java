package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class Controller {
    @Autowired
    private JwtService jwtService;

    @PostMapping("/hello/{text}")
    public String actuator(@PathVariable("text") String text) {
        log.debug("Received a request for greeting with name: {}", text);

        String token = jwtService.generateToken(text);

        List<String> s1 = jwtService.extractRoles(token);
        System.out.println(s1);
        String s2 = jwtService.extractUsername(token);
        System.out.println(s2);

        return token;
    }
}
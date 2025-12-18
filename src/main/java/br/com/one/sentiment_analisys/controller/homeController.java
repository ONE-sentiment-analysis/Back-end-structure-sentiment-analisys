package br.com.one.sentiment_analisys.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class homeController {

    @GetMapping()
    public String helloSpring(){
        return "Olá, Spring boot";
    };

    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Olá, "+ name.toUpperCase().trim();
    }
}

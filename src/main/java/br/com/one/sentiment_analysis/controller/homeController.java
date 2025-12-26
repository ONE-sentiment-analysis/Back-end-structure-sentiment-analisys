package br.com.one.sentiment_analysis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class homeController {

    @GetMapping("/status")
    public String getStatus() {
        return "Serviço está funcionando corretamente!";
    }

    @GetMapping()
    public String helloSpring(){
        return "Olá, Spring boot";
    };

    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Olá, "+ name.toUpperCase().trim();
    }
}

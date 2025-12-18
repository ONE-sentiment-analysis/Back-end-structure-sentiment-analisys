package br.com.one.sentiment_analisys.controller;

import br.com.one.sentiment_analisys.model.SentimentRequestModel;
import br.com.one.sentiment_analisys.model.SentimentResponse;
import br.com.one.sentiment_analisys.service.ExternalApiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/sentiment")
public class SentimentController {

    private final ExternalApiService sentimentService;

    public SentimentController(ExternalApiService sentimentService) {
        this.sentimentService = sentimentService;
    }


    @PostMapping
    public SentimentResponse analisarComentario (@RequestBody SentimentRequestModel text)
            throws IOException, InterruptedException {

        return sentimentService.analisar(text);
    }
}

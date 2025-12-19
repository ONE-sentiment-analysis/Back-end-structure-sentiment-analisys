package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.service.ExternalApiService;
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
    public SentimentResponse analisarComentario (@RequestBody SentimentAnalysisRequest texto)
            throws IOException, InterruptedException {
        return sentimentService.analisar(texto);
    }
}

package br.com.one.sentiment_analysis.dto.response;

import java.util.List;


public record SentimentResponse(
        String status,
        int totalAnalisado,
        List<SentimentItemResponse> avaliacoes
) {}


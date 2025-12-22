package br.com.one.sentiment_analysis.dto.response;

public record SentimentItemResponse(
        String idReferencia,
        String texto,
        String previsao,
        double probabilidade
) {}

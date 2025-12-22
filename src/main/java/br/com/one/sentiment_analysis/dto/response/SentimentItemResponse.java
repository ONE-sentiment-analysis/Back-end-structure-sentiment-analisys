package br.com.one.sentiment_analysis.dto.response;

import java.time.LocalDateTime;

public record SentimentItemResponse(
        String idReferencia,
        String texto,
        String previsao,
        double probabilidade,
        LocalDateTime dataProcessamento
) {}

package br.com.one.sentiment_analysis.dto.response;

import java.time.LocalDateTime;

public record SentimentResponse(
        String idReferencia,
        String texto,
        String previsao,
        double probabilidade,
        String versaoModelo,
        LocalDateTime dataProcessamento
) {}


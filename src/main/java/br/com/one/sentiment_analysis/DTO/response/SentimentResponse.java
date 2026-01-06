package br.com.one.sentiment_analysis.DTO.response;

import java.time.LocalDateTime;

public record SentimentResponse(
        String idReferencia,
        String texto,
        String previsao,
        double probabilidade,
        String versaoModelo,
        LocalDateTime dataProcessamento
) {

    public SentimentResponse {
        if (dataProcessamento == null) {
            dataProcessamento = LocalDateTime.now();
        }
    }
}


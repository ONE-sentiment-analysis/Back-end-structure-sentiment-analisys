package br.com.one.sentiment_analysis.dto.response;

import java.time.LocalDateTime;

public record SentimentResponse(
        String texto,
        String previsao,
        String probabilidadeFormatada,
        String versaoModelo,
        LocalDateTime dataProcessamento
) {

    public SentimentResponse {
        if (dataProcessamento == null) {
            dataProcessamento = LocalDateTime.now();
        }
    }
}


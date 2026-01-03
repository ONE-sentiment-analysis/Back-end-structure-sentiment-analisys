package br.com.one.sentiment_analysis.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SentimentAnalysisRequest(
        @NotBlank(message = "O ID da avaliação é obrigatório")
        String id,

        @NotBlank(message = "O texto é obrigatório")
        @Size(max = 1000, message = "O texto deve ter no máximo 1000 caracteres")
        String text
) {}


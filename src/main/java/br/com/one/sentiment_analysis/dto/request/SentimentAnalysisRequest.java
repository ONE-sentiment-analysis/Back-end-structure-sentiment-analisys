package br.com.one.sentiment_analysis.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SentimentAnalysisRequest(

        @NotEmpty(message = "A lista de avaliações não pode estar vazia")
        @Valid
        List<ReviewRequestItem> reviews
) {}

record ReviewRequestItem(
        @NotBlank(message = "O ID da avaliação é obrigatório")
        String id,

        @NotBlank(message = "O texto é obrigatório")
        @Size(max = 1000, message = "O texto deve ter no máximo 1000 caracteres")
        String text
) {}
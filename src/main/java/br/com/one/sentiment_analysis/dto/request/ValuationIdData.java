package br.com.one.sentiment_analysis.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValuationIdData(
        @NotBlank(message = "O ID da avaliação é obrigatório")
        String id
){ }

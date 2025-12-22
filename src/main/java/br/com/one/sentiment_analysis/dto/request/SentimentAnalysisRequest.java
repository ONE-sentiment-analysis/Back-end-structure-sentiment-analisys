package br.com.one.sentiment_analysis.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SentimentAnalysisRequest(

        @NotEmpty(message = "A lista de avaliações não pode estar vazia")
        @Valid
        List<ReviewRequestItem> reviews
) {}


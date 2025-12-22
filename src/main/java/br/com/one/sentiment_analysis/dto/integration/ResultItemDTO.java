package br.com.one.sentiment_analysis.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

// Inner record para os itens da lista de resposta
public record ResultItemDTO(
        @JsonProperty("id") String id,
        @JsonProperty("sentiment") String sentiment,
        @JsonProperty("probability") double probability
) {}

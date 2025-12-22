package br.com.one.sentiment_analysis.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReviewItemDTO(
        @JsonProperty("id") String id,
        @JsonProperty("text") String text
) {}

package br.com.one.sentiment_analysis.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PythonRequestDTO(
        @JsonProperty("text") String text
) {}



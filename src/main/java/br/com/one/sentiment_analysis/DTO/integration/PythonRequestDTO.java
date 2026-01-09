package br.com.one.sentiment_analysis.DTO.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PythonRequestDTO(
        @JsonProperty("text") String text,

        @JsonProperty("model")  String model
) {}



package br.com.one.sentiment_analysis.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PythonResponseDTO(
        @JsonProperty("model_version") String modelVersion,
        @JsonProperty("processed_at") String processedAt,
        @JsonProperty("sentiment") String sentiment,
        @JsonProperty("probability") double probability
) {}


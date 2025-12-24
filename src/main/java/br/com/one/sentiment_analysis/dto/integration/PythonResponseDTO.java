package br.com.one.sentiment_analysis.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PythonResponseDTO(
        @JsonProperty("model_version") String modelVersion,
        @JsonProperty("processed_at") String processedAt,
        @JsonProperty("results") List<ResultItemDTO> results
) {}


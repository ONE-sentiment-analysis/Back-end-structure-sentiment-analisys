package br.com.one.sentiment_analysis.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PythonResponseDTO(
        @JsonProperty("model_version") String modelVersion,
        @JsonProperty("processed_at") String processedAt, // Recebe como String e convertemos depois
        @JsonProperty("results") List<ResultItemDTO> results
) {}

// Inner record para os itens da lista de resposta
record ResultItemDTO(
        @JsonProperty("id") String id,
        @JsonProperty("sentiment") String sentiment,
        @JsonProperty("probability") double probability
) {}
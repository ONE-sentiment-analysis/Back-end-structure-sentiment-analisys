package br.com.one.sentiment_analysis.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PythonRequestDTO(
        @JsonProperty("request_id") String requestId,
        @JsonProperty("reviews") List<ReviewItemDTO> reviews
) {}


record ReviewItemDTO(
        @JsonProperty("id") String id,
        @JsonProperty("text") String text
) {}
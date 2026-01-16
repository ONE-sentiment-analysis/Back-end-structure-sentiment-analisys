package br.com.one.sentiment_analysis.dto.integration;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PythonResponseDTO(
        @JsonAlias("model")
        @JsonProperty("model_version")
        String modelVersion,

        @JsonAlias("score")
        @JsonProperty("probability")
        double probability,

        @JsonProperty("sentiment")
        String sentiment
) {}
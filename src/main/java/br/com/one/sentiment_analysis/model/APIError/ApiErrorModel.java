package br.com.one.sentiment_analysis.model.APIError;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ApiErrorModel {
    private LocalDateTime timestamp;
    private int statusCode;
    private String error;
    private Map<String, String> messages;

    // Construtor principal
    public ApiErrorModel(int statusCode, String error, Map<String, String> messages) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.error = error;
        this.messages = messages;
    }
}

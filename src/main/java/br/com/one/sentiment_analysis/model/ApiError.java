package br.com.one.sentiment_analysis.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {
    private LocalDateTime timestamp;
    private int statusCode;
    private String error;
    private Map<String, String> messages;

    // Construtor principal
    public ApiError(int statusCode, String error, Map<String, String> messages) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.error = error;
        this.messages = messages;
    }
}

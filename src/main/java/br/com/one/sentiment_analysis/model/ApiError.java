package br.com.one.sentiment_analysis.model;

import lombok.AllArgsConstructor;
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

    public ApiError(LocalDateTime timestamp, int status, String error, Map<String, String> messages) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = status;
        this.error = error;
        this.messages = messages;
    }

    public <V, K> ApiError(int value, String recursoNãoEncontrado, Map<K,V> error) {
    }
}

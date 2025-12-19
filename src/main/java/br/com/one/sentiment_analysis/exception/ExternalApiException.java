package br.com.one.sentiment_analysis.exception;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String msg) {
        super(msg);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

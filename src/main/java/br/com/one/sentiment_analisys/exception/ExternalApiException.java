package br.com.one.sentiment_analisys.exception;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String msg) {
        super(msg);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

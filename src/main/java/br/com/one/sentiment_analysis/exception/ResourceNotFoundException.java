package br.com.one.sentiment_analysis.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException (String msg) {
        super(msg);
    }
}

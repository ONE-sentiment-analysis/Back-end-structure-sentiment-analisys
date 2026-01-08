package br.com.one.sentiment_analysis.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String msg) {
        super(msg);
    }
}

package br.com.one.sentiment_analysis.dto.request;

public record UserLoginRequest(
        String email,
        String password
) {}

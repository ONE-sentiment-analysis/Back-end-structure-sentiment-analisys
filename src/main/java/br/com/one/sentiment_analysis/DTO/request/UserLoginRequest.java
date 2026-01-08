package br.com.one.sentiment_analysis.DTO.request;

public record UserLoginRequest(
        String email,
        String password
) {}

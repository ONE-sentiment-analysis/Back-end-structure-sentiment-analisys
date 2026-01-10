package br.com.one.sentiment_analysis.dto.response;

public record UserLoginResponse(
        String email,
        String token
) {}

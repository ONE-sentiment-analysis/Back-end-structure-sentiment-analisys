package br.com.one.sentiment_analysis.DTO.response;

public record UserLoginResponse(
        String email,
        String token
) {}

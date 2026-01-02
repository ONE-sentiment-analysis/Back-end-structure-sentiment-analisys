package br.com.one.sentiment_analysis.service;

public record PostDTO(
        Long userId,
        Long id,
        String title,
        String body
) {}

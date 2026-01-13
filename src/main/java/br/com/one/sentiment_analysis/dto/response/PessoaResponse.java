package br.com.one.sentiment_analysis.dto.response;

public record PessoaResponse(
        Long id,
        String nome,
        Integer quantidadeAvaliacoes
) {}
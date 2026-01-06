package br.com.one.sentiment_analysis.DTO.response;

public record PessoaResponse(
        Long id,
        String nome,
        Integer quantidadeAvaliacoes
) {}
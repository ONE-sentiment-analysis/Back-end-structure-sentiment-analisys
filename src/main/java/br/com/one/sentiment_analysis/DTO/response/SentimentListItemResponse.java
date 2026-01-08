package br.com.one.sentiment_analysis.DTO.response;

import br.com.one.sentiment_analysis.model.avaliacao.*;

import java.time.LocalDateTime;

public record SentimentListItemResponse(
        IdReferencia idReferencia,
        TextoAvaliacao texto,
        TipoSentimento previsao,
        String probabilidadeFormatada,
        LocalDateTime dataProcessamento
) {
    public SentimentListItemResponse(AnaliseSentimento entidade) {
        this(
                entidade.getIdReferencia(),
                entidade.getTexto(),
                entidade.getPrevisao(),
                entidade.getProbabilidade().asPercentual(),
                entidade.getDataProcessamento()
        );
    }
}

package br.com.one.sentiment_analysis.dto.response;

import br.com.one.sentiment_analysis.model.*;

import java.time.LocalDateTime;

public record SentimentListItemResponse(
        IdReferencia idReferencia,
        TextoAvaliacao texto,
        TipoSentimento previsao,
        Probabilidade probabilidade,
        LocalDateTime dataProcessamento
) {
    public SentimentListItemResponse(AnaliseSentimento entidade) {
        this(
                entidade.getIdReferencia(),
                entidade.getTexto(),
                entidade.getPrevisao(),
                entidade.getProbabilidade(),
                entidade.getDataProcessamento()
        );
    }
}

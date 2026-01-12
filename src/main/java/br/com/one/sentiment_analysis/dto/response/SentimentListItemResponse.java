package br.com.one.sentiment_analysis.dto.response;

import br.com.one.sentiment_analysis.model.avaliacao.*;

import java.time.LocalDateTime;

public record SentimentListItemResponse(
        Long id,
        TextoAvaliacao texto,
        TipoSentimento previsao,
        String probabilidadeFormatada,
        LocalDateTime dataProcessamento
) {
    public SentimentListItemResponse(AnaliseSentimento entidade) {
        this(
                entidade.getId(),
                entidade.getTexto(),
                entidade.getPrevisao(),
                entidade.getProbabilidade().asPercentual(),
                entidade.getDataProcessamento()
        );
    }
}

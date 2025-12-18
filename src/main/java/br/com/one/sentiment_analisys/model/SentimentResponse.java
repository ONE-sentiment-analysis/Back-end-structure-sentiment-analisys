package br.com.one.sentiment_analisys.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SentimentResponse {
    private String previsao;
    private double probabilidade;

    // TODO: adicionar mais parâmentos de acordo com modelo
}

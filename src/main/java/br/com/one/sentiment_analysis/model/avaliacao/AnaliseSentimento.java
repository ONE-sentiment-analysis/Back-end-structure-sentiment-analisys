package br.com.one.sentiment_analysis.model.avaliacao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnaliseSentimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TextoAvaliacao texto;

    @Enumerated(EnumType.STRING)
    private TipoSentimento previsao;

    @Embedded
    private Probabilidade probabilidade;

    @Enumerated(EnumType.STRING)
    private VersaoModelo versaoModelo;

    private LocalDateTime dataProcessamento;

    public AnaliseSentimento(TextoAvaliacao texto, VersaoModelo versaoModelo) {
        this.texto = texto;
        this.versaoModelo = versaoModelo;
    }

    public void registrarResultado(
            TipoSentimento sentimento,
            Probabilidade probabilidade,
            LocalDateTime dataProcessamento
    ) {
        if (sentimento == null) {
            this.previsao = TipoSentimento.NEUTRO;
        } else {
            this.previsao = sentimento;
        }

        this.probabilidade = probabilidade;
        this.dataProcessamento = dataProcessamento;
    }
}
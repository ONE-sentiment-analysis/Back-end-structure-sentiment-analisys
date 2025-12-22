package br.com.one.sentiment_analysis.model;

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
    private IdReferencia idReferencia;

    @Embedded
    private TextoAvaliacao texto;

    @Enumerated(EnumType.STRING)
    private TipoSentimento previsao;

    private Probabilidade probabilidade;

    private String versaoModelo;
    private LocalDateTime dataProcessamento;

    public AnaliseSentimento(TextoAvaliacao texto, IdReferencia idReferencia) {
        this.idReferencia = new IdReferencia(idReferencia.getValor());
        this.texto = texto;
    }

    public void registrarResultado(TipoSentimento sentimento, Probabilidade probabilidade,
                                   String versaoModelo, LocalDateTime dataProcessamento) {
        if (sentimento == null) {
            throw new IllegalArgumentException("O sentimento calculado não pode ser nulo");
        }

        this.previsao = sentimento;
        this.probabilidade = probabilidade;
        this.versaoModelo = versaoModelo;
        this.dataProcessamento = dataProcessamento;
    }
}
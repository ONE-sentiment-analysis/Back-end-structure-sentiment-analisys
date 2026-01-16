package br.com.one.sentiment_analysis.model.avaliacao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

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
        if (probabilidade == null) {
            throw new IllegalArgumentException("Probabilidade não pode ser nula");
        }

        if (dataProcessamento == null) {
            throw new IllegalArgumentException("Data de processamento não pode ser nula");
        }

        this.previsao = Objects.requireNonNullElse(sentimento, TipoSentimento.NEUTRO);

        this.probabilidade = probabilidade;
        this.dataProcessamento = dataProcessamento;
    }

    public void atualizarAvaliacao (TextoAvaliacao texto, VersaoModelo versaoModelo) {
        this.texto = texto;
        this.versaoModelo = versaoModelo;
    }
}
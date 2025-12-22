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
    //Todo: Precisa ser discutido uma regra de negócios para o tamanho máximo de caracteres em uma avaliação
    private static final int TAMANHO_MAXIMO_TEXTO = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "id_referencia", unique = true, nullable = false))
    private IdReferencia idReferencia;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "texto", nullable = false, length = 1000))
    private TextoAvaliacao texto;

    @Enumerated(EnumType.STRING)
    private TipoSentimento previsao;

    private Probabilidade probabilidade;

    private String versaoModelo;
    private LocalDateTime dataProcessamento;

    public AnaliseSentimento(TextoAvaliacao texto, IdReferencia idReferencia) {
        this.idReferencia = idReferencia;
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
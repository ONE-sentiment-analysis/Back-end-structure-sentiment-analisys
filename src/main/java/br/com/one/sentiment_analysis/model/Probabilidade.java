package br.com.one.sentiment_analysis.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Probabilidade {

    @Column(name = "probabilidade")
    @JsonValue
    private double valor;

    public Probabilidade(double valor) {
        if (valor < 0 || valor > 1) {
            throw new IllegalArgumentException("A probabilidade deve estar entre 0 e 1");
        }
        this.valor = valor;
    }

    public String asPercentual() {
        return String.format("%.1f%%", valor * 100);
    }
}

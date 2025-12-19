package br.com.one.sentiment_analysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Necessário para o Hibernate/JPA
public class TextoAvaliacao {

    private static final int TAMANHO_MAXIMO = 1000;
    private static final int TAMANHO_MINIMO = 5;

    @Column(name = "texto", nullable = false, length = TAMANHO_MAXIMO)
    private String valor;

    public TextoAvaliacao(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O texto não pode ser nulo ou vazio");
        }
        if (valor.length() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("O texto excede o limite de " + TAMANHO_MAXIMO + " caracteres.");
        }
        if (valor.length() < TAMANHO_MINIMO) {
            throw new IllegalArgumentException("O texto precisa atingir o mínimo de " + TAMANHO_MINIMO + " caracteres.");
        }
        this.valor = valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}

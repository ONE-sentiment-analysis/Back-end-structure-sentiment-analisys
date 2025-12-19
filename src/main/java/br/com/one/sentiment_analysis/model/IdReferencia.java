package br.com.one.sentiment_analysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA requires this
public class IdReferencia implements Serializable {

    // Compilamos o padrão apenas uma vez para performance (static final)
    private static final String REGEX_PADRAO = "^prod_\\d+_review_\\d+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX_PADRAO);

    @Column(name = "id_referencia", nullable = false, unique = true)
    private String valor;

    public IdReferencia(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O ID de referência é obrigatório.");
        }

        // Validação do Regex
        if (!PATTERN.matcher(valor).matches()) {
            throw new IllegalArgumentException("O ID '" + valor + "' está fora do padrão esperado (ex: prod_123_review_456).");
        }

        this.valor = valor;
    }

    // Facilita o uso do objeto como String normal quando necessário
    @Override
    public String toString() {
        return valor;
    }

    // Importante implementar equals/hashCode para Value Objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdReferencia that)) return false;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }
}

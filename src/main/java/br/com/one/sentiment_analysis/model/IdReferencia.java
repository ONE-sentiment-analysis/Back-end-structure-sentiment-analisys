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
    private static final String REGEX_PADRAO = "^prod_\\d+_review_\\d+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX_PADRAO);

    @Column(name = "id_referencia", nullable = false, unique = true)
    private String valor;

    public IdReferencia(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O ID de referência é obrigatório.");
        }

        if (!PATTERN.matcher(valor).matches()) {
            throw new IllegalArgumentException("O ID '" + valor + "' está fora do padrão esperado (ex: prod_123_review_456).");
        }

        this.valor = valor;
    }

    public Long getIdProduto() {
        try {
            String[] partes = this.valor.split("_");
            return Long.parseLong(partes[1]);
        } catch (Exception e) {
            throw new IllegalStateException("O formato do ID de referência é inválido para extração.", e);
        }
    }


    @Override
    public String toString() {
        return valor;
    }

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

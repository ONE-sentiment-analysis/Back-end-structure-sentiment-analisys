package br.com.one.sentiment_analysis.model.avaliacao;

import br.com.one.sentiment_analysis.dto.request.IdentificadorReferencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IdReferenciaTest {

    @Test
    @DisplayName("Deve criar um IdReferencia válido quando o formato estiver correto")
    void idReferencia_cenario1() {
        String valorValido = "prod_123_review_456";
        IdReferencia idReferencia = new IdReferencia(valorValido);

        assertNotNull(idReferencia);
        assertEquals(valorValido, idReferencia.getValor());
        assertEquals(valorValido, idReferencia.toString());
    }

    @Test
    @DisplayName("Deve extrair os IDs numéricos corretamente (Produto e Review)")
    void idReferencia_cenario2() {
        IdReferencia idReferencia = new IdReferencia("prod_500_review_99");

        IdentificadorReferencia extraido = idReferencia.getIdAvaliacaoExtraido();

        assertNotNull(extraido);
        assertEquals(500L, extraido.idProduto());
        assertEquals(99L, extraido.idReview());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Deve lançar IllegalArgumentException quando o valor for vazio, branco, apenas com tabulações ou nulo")
    void idReferencia_cenario3(String valorInvalido) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new IdReferencia(valorInvalido));

        assertEquals("O ID de referência é obrigatório.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "produto_123_review_456", // Prefixo errado
            "prod_abc_review_456",    // Letras onde deveria haver números
            "prod_123_analise_456",   // "analise" em vez de "review"
            "prod_123",               // Formato incompleto
            "123_456",                // Sem prefixos
            "prod_123_review_",       // Sem ID do review
            "prod_review_456"         // Sem ID do prod
    })
    @DisplayName("Deve lançar IllegalArgumentException para formatos fora do padrão Regex")
    void idReferencia_cenario5(String formatoInvalido) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new IdReferencia(formatoInvalido));

        assertTrue(exception.getMessage().contains("está fora do padrão esperado"));
    }

    @Test
    @DisplayName("Deve verificar igualdade (equals e hashCode) entre objetos com mesmo valor")
    void idReferencia_cenario6() {
        IdReferencia id1 = new IdReferencia("prod_10_review_20");
        IdReferencia id2 = new IdReferencia("prod_10_review_20");
        IdReferencia id3 = new IdReferencia("prod_99_review_99");

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);

        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}

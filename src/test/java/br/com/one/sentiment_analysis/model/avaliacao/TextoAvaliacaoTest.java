package br.com.one.sentiment_analysis.model.avaliacao;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TextoAvaliacaoTest {

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 500, 999, 1000})
    @DisplayName("Deve criar texto válido para limites exatos (5 e 1000) e valores intermediários")
    void textoAvaliacao_cenario1(int length) {
        String text = "a".repeat(length);

        TextoAvaliacao textoAvaliacao = new TextoAvaliacao(text);

        assertAll("Validar criação bem-sucedida",
                () -> assertNotNull(textoAvaliacao),
                () -> assertEquals(text, textoAvaliacao.getValor()),
                () -> assertEquals(text, textoAvaliacao.toString(), "toString deve retornar o valor original")
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para textos abaixo do limite mínimo (< 5)")
    void textoAvaliacao_cenario2() {
        String text = "abcd";

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> new TextoAvaliacao(text)
        );

        assertEquals("O texto precisa atingir o mínimo de 5 caracteres.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para textos acima do limite máximo (> 1000)")
    void textoAvaliacao_cenario3() {
        String text = "a".repeat(1001);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> new TextoAvaliacao(text)
        );

        assertEquals("O texto excede o limite de 1000 caracteres.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o texto for nulo")
    void textoAvaliacao_cenario4() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new TextoAvaliacao(null));

        assertEquals("O texto não pode ser nulo ou vazio.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Deve rejeitar textos em branco")
    void deveRejeitarNulosOuBrancos(String invalidText) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new TextoAvaliacao(invalidText));
        assertEquals("O texto não pode ser nulo ou vazio.", ex.getMessage());
    }
}

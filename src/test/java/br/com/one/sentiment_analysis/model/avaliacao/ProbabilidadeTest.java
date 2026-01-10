package br.com.one.sentiment_analysis.model.avaliacao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ProbabilidadeTest {

    private Locale localePadrao;

    @BeforeEach
    void setUp() {
        localePadrao = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(localePadrao);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.5, 1.0})
    @DisplayName("Deve criar probabilidade válida para valores dentro do intervalo [0, 1]")
    void probabilidade_cenario1(double valor) {
        Probabilidade probabilidade = new Probabilidade(valor);
        assertEquals(valor, probabilidade.getValor());
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.0001, -0.0001, 1.1, -0.1, 25.0})
    @DisplayName("Deve lançar exceção para valores fora do intervalo [0, 1]")
    void probabilidade_cenario2(double valor) {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> new Probabilidade(valor));

        assertEquals("A probabilidade deve estar entre 0 e 1", exception.getMessage());
    }

    @Test
    @DisplayName("Deve formatar para porcentagem considerando Locale PT-BR (vírgula)")
    void probabilidade_cenario3() {
        Probabilidade p1 = new Probabilidade(0.5);
        assertEquals("50.0%", p1.asPercentual());
    }

    @Test
    @DisplayName("Deve arredondar corretamente (cima e baixo)")
    void probabilidade_cenario4() {
        assertEquals("12.3%", new Probabilidade(0.12345).asPercentual());

        assertEquals("12.4%", new Probabilidade(0.12360).asPercentual());

        assertEquals("12.4%", new Probabilidade(0.12350).asPercentual());
    }
}
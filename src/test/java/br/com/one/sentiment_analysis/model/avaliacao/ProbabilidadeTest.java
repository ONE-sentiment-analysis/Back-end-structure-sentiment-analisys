package br.com.one.sentiment_analysis.model.avaliacao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
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
    @ValueSource(doubles = {0.0, 0.0001, 0.5, 0.9999, 1.0})
    @DisplayName("Deve criar probabilidade válida para valores dentro do intervalo [0, 1]")
    void probabilidade_cenario1(double valor) {
        Probabilidade probabilidade = new Probabilidade(valor);
        assertEquals(valor, probabilidade.getValor(), 0.00001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.00001, -1.0, 1.00001, 1.5, 100.0})
    @DisplayName("Deve lançar exceção para valores fora do intervalo [0, 1]")
    void probabilidade_cenario2(double valor) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Probabilidade(valor)
        );

        assertEquals("A probabilidade deve estar entre 0 e 1", ex.getMessage());
    }

    @Test
    @DisplayName("Deve formatar percentual corretamente com Locale PT-BR")
    void deveFormatarPercentual() {
        Locale.setDefault(new Locale("pt", "BR"));

        assertAll("Validação de formatação de strings",
                () -> assertEquals("50,0%", new Probabilidade(0.5).asPercentual()),
                () -> assertEquals("12,3%", new Probabilidade(0.12345).asPercentual(), "Deve arredondar para baixo"),
                () -> assertEquals("12,4%", new Probabilidade(0.12360).asPercentual(), "Deve arredondar para cima"),
                () -> assertEquals("0,0%", new Probabilidade(0.0).asPercentual()),
                () -> assertEquals("100,0%", new Probabilidade(1.0).asPercentual())
        );
    }
}
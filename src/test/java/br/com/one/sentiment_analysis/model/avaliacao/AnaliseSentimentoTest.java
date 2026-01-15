package br.com.one.sentiment_analysis.model.avaliacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AnaliseSentimentoTest {

    @Test
    @DisplayName("Deve instanciar corretamente com Texto e Versão do Modelo")
    void analiseSentimentoTest_cenario1() {
        TextoAvaliacao texto = new TextoAvaliacao("Produto muito bom");
        VersaoModelo versaoModelo = VersaoModelo.LOGISTIC_REGRESSION;

        AnaliseSentimento analise = new AnaliseSentimento(texto, versaoModelo);

        assertAll("Estado Inicial",
                () -> assertEquals(texto, analise.getTexto()),
                () -> assertEquals(versaoModelo, analise.getVersaoModelo()),
                () -> assertNull(analise.getPrevisao()),
                () -> assertNull(analise.getProbabilidade()),
                () -> assertNull(analise.getDataProcessamento())
        );
    }

    @Test
    @DisplayName("Deve registrar o resultado completo com dados válidos")
    void analiseSentimentoTest_cenario2() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Teste"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.99);
        LocalDateTime dataHora = LocalDateTime.now();

        analise.registrarResultado(TipoSentimento.POSITIVO, probabilidade, dataHora);

        assertAll("Estado Pós-Resultado",
                () -> assertEquals(TipoSentimento.POSITIVO, analise.getPrevisao()),
                () -> assertEquals(probabilidade, analise.getProbabilidade()),
                () -> assertEquals(VersaoModelo.LOGISTIC_REGRESSION, analise.getVersaoModelo()),
                () -> assertEquals(dataHora, analise.getDataProcessamento())
        );
    }

    @Test
    @DisplayName("Deve assumir NEUTRO se sentimento for nulo")
    void analiseSentimentoTest_cenario3() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Texto qualquer"), VersaoModelo.MULTINOMIAL_NB);

        analise.registrarResultado(null, new Probabilidade(0.5), LocalDateTime.now());

        assertEquals(TipoSentimento.NEUTRO, analise.getPrevisao(),
                "A previsão deveria ser assumida como NEUTRO quando nula");
    }

    @Test
    @DisplayName("Validações de argumentos nulos no registro de resultado")
    void analiseSentimentoTest_cenario4() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Teste"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade prob = new Probabilidade(0.5);
        LocalDateTime agora = LocalDateTime.now();

        assertAll("Exceções de Argumentos",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> analise.registrarResultado(TipoSentimento.POSITIVO, null, agora), "Deve exigir probabilidade"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> analise.registrarResultado(TipoSentimento.POSITIVO, prob, null), "Deve exigir data")
        );
    }

    @Test
    @DisplayName("Deve permitir atualização de resultado")
    void analiseSentimentoTest_cenario5() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Texto qualquer"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.95);
        LocalDateTime dataHora = LocalDateTime.now();

        analise.registrarResultado(TipoSentimento.POSITIVO, probabilidade, dataHora);

        assertAll("Verificação Pré-Atualização",
                () -> assertEquals(TipoSentimento.POSITIVO, analise.getPrevisao()),
                () -> assertEquals(probabilidade, analise.getProbabilidade()),
                () -> assertEquals(VersaoModelo.LOGISTIC_REGRESSION, analise.getVersaoModelo()),
                () -> assertEquals(dataHora, analise.getDataProcessamento())
        );

        analise.atualizarAvaliacao(new TextoAvaliacao("Outro texto qualquer"), VersaoModelo.RANDOM_FOREST);
        Probabilidade novaProbabilidade = new Probabilidade(0.86);
        LocalDateTime finalDataHora = LocalDateTime.now();

        analise.registrarResultado(TipoSentimento.NEUTRO, novaProbabilidade, finalDataHora);

        assertAll("Verificação Pós-Atualização",
                () -> assertEquals(TipoSentimento.NEUTRO, analise.getPrevisao()),
                () -> assertEquals(novaProbabilidade, analise.getProbabilidade()),
                () -> assertEquals(VersaoModelo.RANDOM_FOREST, analise.getVersaoModelo()),
                () -> assertEquals(finalDataHora, analise.getDataProcessamento())
        );
    }
}
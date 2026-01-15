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
        String versaoModelo = "LogisticRegression";

        AnaliseSentimento analise = new AnaliseSentimento(texto, VersaoModelo.fromString(versaoModelo));

        assertEquals(texto, analise.getTexto());
        assertEquals(VersaoModelo.LOGISTIC_REGRESSION, analise.getVersaoModelo());
        assertNull(analise.getPrevisao());
        assertNull(analise.getProbabilidade());
        assertNull(analise.getDataProcessamento());
    }

    @Test
    @DisplayName("Deve registrar o resultado completo com dados válidos")
    void analiseSentimentoTest_cenario2() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Teste válido"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.95);
        LocalDateTime dataHora = LocalDateTime.now();

        analise.registrarResultado(TipoSentimento.POSITIVO, probabilidade, dataHora);

        assertEquals(TipoSentimento.POSITIVO, analise.getPrevisao());
        assertEquals(probabilidade, analise.getProbabilidade());
        assertEquals(VersaoModelo.LOGISTIC_REGRESSION, analise.getVersaoModelo());
        assertEquals(dataHora, analise.getDataProcessamento());
    }

    @Test
    @DisplayName("Deve aplicar \"Default\" (NEUTRO) quando o sentimento for Nulo")
    void analiseSentimentoTest_cenario3() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Texto qualquer"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.5);

        analise.registrarResultado(null, probabilidade, LocalDateTime.now());

        assertEquals(TipoSentimento.NEUTRO, analise.getPrevisao(),
                "A previsão deveria ser assumida como NEUTRO quando nula");
    }

    @Test
    @DisplayName("Deve rejeitar probabilidade nula")
    void analiseSentimentoTest_cenario4() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Texto qualquer"), VersaoModelo.LOGISTIC_REGRESSION);

        assertThrows(IllegalArgumentException.class, () -> analise.registrarResultado(TipoSentimento.POSITIVO, null, LocalDateTime.now()));
    }

    @Test
    @DisplayName("Deve rejeitar data de processamento nula")
    void analiseSentimentoTest_cenario5() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Texto qualquer"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.5);

        assertThrows(IllegalArgumentException.class, () -> analise.registrarResultado(TipoSentimento.POSITIVO, probabilidade, null));
    }

    @Test
    @DisplayName("Deve permitir atualização de resultado")
    void analiseSentimentoTest_cenario6() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Texto qualquer"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.95);
        LocalDateTime dataHora = LocalDateTime.now();

        analise.registrarResultado(TipoSentimento.POSITIVO, probabilidade, dataHora);

        assertEquals(TipoSentimento.POSITIVO, analise.getPrevisao());
        assertEquals(probabilidade, analise.getProbabilidade());
        assertEquals(VersaoModelo.LOGISTIC_REGRESSION, analise.getVersaoModelo());
        assertEquals(dataHora, analise.getDataProcessamento());

        analise.atualizarAvaliacao(new TextoAvaliacao("Outro texto qualquer"), VersaoModelo.RANDOM_FOREST);

        Probabilidade novaProbabilidade = new Probabilidade(0.86);

        dataHora = LocalDateTime.now();

        analise.registrarResultado(TipoSentimento.NEUTRO, novaProbabilidade, dataHora);


        assertEquals(TipoSentimento.NEUTRO, analise.getPrevisao());
        assertEquals(novaProbabilidade, analise.getProbabilidade());
        assertEquals(VersaoModelo.RANDOM_FOREST, analise.getVersaoModelo());
        assertEquals(dataHora, analise.getDataProcessamento());
    }
}
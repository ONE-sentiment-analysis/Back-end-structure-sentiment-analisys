package br.com.one.sentiment_analysis.model.avaliacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AnaliseSentimentoTest {

    @Test
    @DisplayName("Deve instanciar corretamente com Texto e Versão do Modelo")
    void deveInstanciarComTexto() {
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
    void deveRegistrarResultadoCorretamente() {
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
    void deveDefinirNeutroQuandoSentimentoNulo() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Texto qualquer"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.5);

        analise.registrarResultado(null, probabilidade, LocalDateTime.now());

        assertEquals(TipoSentimento.NEUTRO, analise.getPrevisao(),
                "A previsão deveria ser assumida como NEUTRO quando nula");
    }
}
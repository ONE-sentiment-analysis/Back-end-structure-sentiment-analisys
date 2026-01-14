package br.com.one.sentiment_analysis.model.avaliacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AnaliseSentimentoTest {

    @Test
    @DisplayName("Deve instanciar corretamente apenas com o texto e modelo")
    void deveInstanciarComTexto() {
        TextoAvaliacao texto = new TextoAvaliacao("Produto muito bom");
        String versaoModelo = "LogisticRegression";

        AnaliseSentimento analise = new AnaliseSentimento(texto, VersaoModelo.valueOf(versaoModelo));

        assertEquals(texto, analise.getTexto());
        assertNull(analise.getPrevisao());
        assertNull(analise.getProbabilidade());
        assertNull(analise.getDataProcessamento());
    }

    @Test
    @DisplayName("Deve registrar o resultado corretamente com dados válidos")
    void deveRegistrarResultadoCorretamente() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Teste válido"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.95);
        String versao = "v1.0";
        LocalDateTime dataHora = LocalDateTime.now();

        analise.registrarResultado(TipoSentimento.POSITIVO, probabilidade, dataHora);

        assertEquals(TipoSentimento.POSITIVO, analise.getPrevisao());
        assertEquals(probabilidade, analise.getProbabilidade());
        assertEquals("v1.0", analise.getVersaoModelo());
        assertEquals(dataHora, analise.getDataProcessamento());
    }

    @Test
    @DisplayName("Deve definir sentimento como NEUTRO quando o sentimento informado for nulo")
    void deveDefinirNeutroQuandoSentimentoNulo() {
        AnaliseSentimento analise = new AnaliseSentimento(new TextoAvaliacao("Texto qualquer"), VersaoModelo.LOGISTIC_REGRESSION);
        Probabilidade probabilidade = new Probabilidade(0.5);

        analise.registrarResultado(null, probabilidade, LocalDateTime.now());

        assertEquals(TipoSentimento.NEUTRO, analise.getPrevisao(),
                "A previsão deveria ser assumida como NEUTRO quando nula");
    }
}
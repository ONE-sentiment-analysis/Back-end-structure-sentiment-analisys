package br.com.one.sentiment_analysis.model.avaliacao;

import br.com.one.sentiment_analysis.DTO.response.SentimentResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SentimentResponseTest {

    @Test
    void shouldCreateAnalysisWithTextAndReferenceId() {
        TextoAvaliacao text = new TextoAvaliacao("This is a valid text");
        IdReferencia referenceId = new IdReferencia("123");

        AnaliseSentimento analysis = new AnaliseSentimento(text, referenceId);

        assertNotNull(analysis.getTexto());
        assertEquals("This is a valid text", analysis.getTexto().getValor());
        assertEquals("123", analysis.getIdReferencia().getValor());
        assertNull(analysis.getPrevisao());
    }

    @Test
    void shouldCreateSentimentResponse() {
        LocalDateTime now = LocalDateTime.now();

        SentimentResponse response = new SentimentResponse(
                "123",
                "not as bad as I thought",
                "Positive",
                "95%",
                "v1.0",
                now
        );

        assertEquals("123", response.idReferencia());
        assertEquals("not as bad as I thought", response.texto());
        assertEquals("Positive", response.previsao());
        assertEquals("95%", response.probabilidadeFormatada());
        assertEquals("v1.0", response.versaoModelo());
        assertEquals(now, response.dataProcessamento());
    }

    @Test
    void shouldRegisterResultWithValidSentiment() {
        TextoAvaliacao text = new TextoAvaliacao("Text for analysis");
        IdReferencia referenceId = new IdReferencia("456");
        AnaliseSentimento analysis = new AnaliseSentimento(text, referenceId);

        Probabilidade probability = new Probabilidade(0.85);
        LocalDateTime now = LocalDateTime.now();

        analysis.registrarResultado(TipoSentimento.POSITIVO, probability, "v1.0", now);

        assertEquals(TipoSentimento.POSITIVO, analysis.getPrevisao());
        assertEquals(probability, analysis.getProbabilidade());
        assertEquals("v1.0", analysis.getVersaoModelo());
        assertEquals(now, analysis.getDataProcessamento());
    }

    @Test
    void shouldRegisterResultWithNullSentimentAndDefaultToNeutral() {
        TextoAvaliacao text = new TextoAvaliacao("Text for analysis");
        IdReferencia referenceId = new IdReferencia("789");
        AnaliseSentimento analysis = new AnaliseSentimento(text, referenceId);

        Probabilidade probability = new Probabilidade(0.50);
        LocalDateTime now = LocalDateTime.now();

        analysis.registrarResultado(null, probability, "v2.0", now);

        assertEquals(TipoSentimento.NEUTRO, analysis.getPrevisao());
        assertEquals(probability, analysis.getProbabilidade());
        assertEquals("v2.0", analysis.getVersaoModelo());
        assertEquals(now, analysis.getDataProcessamento());
    }
}

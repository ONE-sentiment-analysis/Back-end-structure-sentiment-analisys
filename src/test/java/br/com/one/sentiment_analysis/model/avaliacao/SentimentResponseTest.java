package br.com.one.sentiment_analysis.DTO.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SentimentResponseTest {

    @Test
    void testRecordValues() {
        LocalDateTime now = LocalDateTime.now();

        SentimentResponse response = new SentimentResponse(
                "123",
                "não é tão ruim quanto pensei",
                "Positivo",
                "95%",
                "v1.0",
                now
        );

        assertEquals("123", response.idReferencia());
        assertEquals("não é tão ruim quanto pensei", response.texto());
        assertEquals("Positivo", response.previsao());
        assertEquals("95%", response.probabilidadeFormatada());
        assertEquals("v1.0", response.versaoModelo());
        assertEquals(now, response.dataProcessamento());
    }
}
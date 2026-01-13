package br.com.one.sentiment_analysis.model.avaliacao;

import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SentimentResponseTest {

    @Test
    void testRecordValues() {
        LocalDateTime now = LocalDateTime.now();

        SentimentResponse response = new SentimentResponse(
                "não é tão ruim quanto pensei",
                "Positivo",
                "95%",
                "v1.0",
                now
        );

        assertEquals("não é tão ruim quanto pensei", response.texto());
        assertEquals("Positivo", response.previsao());
        assertEquals("95%", response.probabilidadeFormatada());
        assertEquals("v1.0", response.versaoModelo());
        assertEquals(now, response.dataProcessamento());
    }
}
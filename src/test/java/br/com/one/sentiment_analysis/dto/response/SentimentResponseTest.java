package br.com.one.sentiment_analysis.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class SentimentResponseTest {

    @Test
    @DisplayName("Deve manter a data informada quando ela não for nula")
    void sentimentResponse_cenario1() {
        String texto = "Produto excelente";
        String previsao = "POSITIVO";
        String probabilidade = "98.5%";
        String versao = "rf";
        LocalDateTime dataEspecifica = LocalDateTime.of(2026, 1, 1, 10, 0, 0);

        SentimentResponse response = new SentimentResponse(
                texto,
                previsao,
                probabilidade,
                versao,
                dataEspecifica
        );

        assertAll("Verificação de persistência da data informada",
                () -> assertEquals(texto, response.texto()),
                () -> assertEquals(previsao, response.previsao()),
                () -> assertEquals(probabilidade, response.probabilidadeFormatada()),
                () -> assertEquals(versao, response.versaoModelo()),
                () -> assertEquals(dataEspecifica, response.dataProcessamento(), "A data não deveria ter sido alterada")
        );
    }

    @Test
    @DisplayName("Deve gerar data atual automaticamente quando dataProcessamento for nula")
    void sentimentResponse_cenario2() {
        LocalDateTime antes = LocalDateTime.now();

        SentimentResponse response = new SentimentResponse(
                "Teste de data nula",
                "NEUTRO",
                "50.0%",
                "lr",
                null
        );

        LocalDateTime depois = LocalDateTime.now();

        assertAll("Verificação da geração automática de data",
                () -> assertNotNull(response.dataProcessamento(), "A data de processamento não pode ser nula"),
                () -> assertFalse(response.dataProcessamento().isBefore(antes), "A data gerada é muito antiga"),
                () -> assertFalse(response.dataProcessamento().isAfter(depois), "A data gerada está no futuro")
        );
    }

    @Test
    @DisplayName("Deve instanciar corretamente com campos vazios (exceto data que precisa ter algum valor ou ser nula)")
    void sentimentResponse_cenario3() {
        SentimentResponse response = new SentimentResponse("", "", "", "", null);

        assertAll("Verificação com strings vazias",
                () -> assertNotNull(response),
                () -> assertTrue(response.texto().isEmpty()),
                () -> assertNotNull(response.dataProcessamento())
        );
    }
}
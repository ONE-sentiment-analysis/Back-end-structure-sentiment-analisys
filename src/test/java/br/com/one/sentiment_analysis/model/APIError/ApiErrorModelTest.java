package br.com.one.sentiment_analysis.model.APIError;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class ApiErrorModelTest {

    @Test
    @DisplayName("Construtor personalizado deve inicializar campos e gerar timestamp atual")
    void apiErrorModel_cenario1() {
        int statusCodeEsperado = 400;
        String erroEsperado = "Erro de Negócio";
        Map<String, String> mensagensEsperadas = Map.of("erro", "detalhe do erro");

        LocalDateTime antesDaInstancia = LocalDateTime.now();

        ApiErrorModel apiError = new ApiErrorModel(statusCodeEsperado, erroEsperado, mensagensEsperadas);

        LocalDateTime depoisDaInstancia = LocalDateTime.now();

        assertAll("Verificação do estado do objeto ApiErrorModel",
                () -> assertEquals(statusCodeEsperado, apiError.getStatusCode(), "Status code incorreto"),
                () -> assertEquals(erroEsperado, apiError.getError(), "Mensagem de erro incorreta"),
                () -> assertEquals(mensagensEsperadas, apiError.getMessages(), "Mapa de mensagens incorreto"),
                () -> assertNotNull(apiError.getTimestamp(), "Timestamp não deveria ser nulo"),

                () -> assertTrue(apiError.getTimestamp().isAfter(antesDaInstancia.minusNanos(1)) || apiError.getTimestamp().isEqual(antesDaInstancia), "Timestamp muito antigo"),
                () -> assertTrue(apiError.getTimestamp().isBefore(depoisDaInstancia.plusNanos(1)) || apiError.getTimestamp().isEqual(depoisDaInstancia), "Timestamp no futuro")
        );
    }

    @Test
    @DisplayName("Construtor padrão (Lombok) deve criar instância vazia para serialização")
    void apiErrorModel_cenario2() {
        ApiErrorModel apiError = new ApiErrorModel();

        assertAll("Verificação do construtor padrão (NoArgsConstructor)",
                () -> assertNotNull(apiError, "Instância não deveria ser nula"),
                () -> assertEquals(0, apiError.getStatusCode(), "Int primitivo deve iniciar como 0"),
                () -> assertNull(apiError.getError(), "String deve iniciar como null"),
                () -> assertNull(apiError.getMessages(), "Map deve iniciar como null"),
                () -> assertNull(apiError.getTimestamp(), "Timestamp deve ser null pois a lógica está apenas no construtor personalizado")
        );
    }

    @Test
    @DisplayName("Deve aceitar parâmetros nulos sem lançar exceção")
    void apiErrorModel_cenario3() {
        int statusCode = 500;

        ApiErrorModel apiError = new ApiErrorModel(statusCode, null, null);

        assertAll("Validação de robustez com nulos",
                () -> assertEquals(statusCode, apiError.getStatusCode()),
                () -> assertNull(apiError.getError()),
                () -> assertNull(apiError.getMessages()),
                () -> assertNotNull(apiError.getTimestamp(), "Timestamp deve ser gerado independentemente dos argumentos")
        );
    }
}
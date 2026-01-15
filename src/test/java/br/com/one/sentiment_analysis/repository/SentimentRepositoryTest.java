package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.avaliacao.AnaliseSentimento;
import br.com.one.sentiment_analysis.model.avaliacao.Probabilidade;
import br.com.one.sentiment_analysis.model.avaliacao.TextoAvaliacao;
import br.com.one.sentiment_analysis.model.avaliacao.TipoSentimento;
import br.com.one.sentiment_analysis.model.avaliacao.VersaoModelo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SentimentRepositoryTest {

    @Autowired
    private SentimentRepository repository;

    @Test
    @DisplayName("Deve salvar análise e recuperar via paginação")
    void sentimentRepositoryTest_cenario1() {
        AnaliseSentimento analise = new AnaliseSentimento(
                new TextoAvaliacao("Teste repository"),
                VersaoModelo.LOGISTIC_REGRESSION
        );
        analise.registrarResultado(TipoSentimento.POSITIVO, new Probabilidade(0.88), LocalDateTime.now());

        AnaliseSentimento salvo = repository.save(analise);

        Page<AnaliseSentimento> page = repository.findAllById(salvo.getId(), PageRequest.of(0, 10));

        assertAll("Busca Paginada por ID",
                () -> assertNotNull(page),
                () -> assertFalse(page.isEmpty()),
                () -> assertEquals(1, page.getTotalElements()),
                () -> assertEquals(salvo.getId(), page.getContent().getFirst().getId()),
                () -> assertEquals(TipoSentimento.POSITIVO, page.getContent().getFirst().getPrevisao())
        );
    }

    @Test
    @DisplayName("Deve retornar página vazia para ID inexistente")
    void sentimentRepositoryTest_cenario2() {
        Page<AnaliseSentimento> page = repository.findAllById(999L, PageRequest.of(0, 10));

        assertNotNull(page);
        assertTrue(page.isEmpty());
    }
}
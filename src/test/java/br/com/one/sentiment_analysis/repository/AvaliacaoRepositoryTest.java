package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
class AvaliacaoRepositoryTest {

    @Autowired
    private AvaliacaoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar e recuperar uma análise completa com sucesso")
    void cadastrar_cenario_1() {
        AnaliseSentimento analise = criarAnalisePadrao("prod_10_review_1", "Produto excelente!");

        AnaliseSentimento salvo = repository.save(analise);

        Assertions.assertNotNull(salvo.getId());

        Optional<AnaliseSentimento> buscado = repository.findById(salvo.getId());

        Assertions.assertTrue(buscado.isPresent());

        Assertions.assertEquals(
                "prod_10_review_1",
                buscado.get().getIdReferencia().getValor(),
                "Verifica se o IdReferencia foi gravada corretamente"
        );

        Assertions.assertEquals(
                TipoSentimento.POSITIVO,
                buscado.get().getPrevisao(),
                "Verifica se o sentimento foi gravada corretamente"
        );

        Assertions.assertEquals(
                0.95,
                buscado.get().getProbabilidade().getValor(),
                "Verifica se a probabilidade foi gravada corretamente"
        );

        Assertions.assertEquals(
                "v1-test",
                buscado.get().getVersaoModelo(),
                "Verifica se a versão do modelo foi gravada corretamente"
        );

        Assertions.assertNotNull(
                buscado.get().getDataProcessamento(),
                "Verifica se a data não é nula"
        );

        Assertions.assertEquals(
                analise.getDataProcessamento(),
                buscado.get().getDataProcessamento(),
                "Verifica se a data de processamento foi gravada corretamente"
        );
    }

    @Test
    @DisplayName("Deve buscar avaliações filtrando pelo ID do produto (Query Customizada)")
    void deveBuscarPorIdProduto() {
        repository.save(criarAnalisePadrao("prod_50_review_100", "Gostei muito"));
        repository.save(criarAnalisePadrao("prod_50_review_101", "Achei mediano"));

        repository.save(criarAnalisePadrao("prod_99_review_200", "Outro produto"));

        entityManager.flush();

        Page<AnaliseSentimento> resultado = repository.buscarPorIdProduto(50L, PageRequest.of(0, 10));

        Assertions.assertEquals(2, resultado.getTotalElements(), "Deveria achar 2 reviews do produto 50");

        boolean contemErrado = resultado.getContent().stream()
                .anyMatch(a -> a.getIdReferencia().getValor().contains("prod_99"));
        Assertions.assertFalse(contemErrado, "Não deveria trazer reviews do produto 99");
    }

    @Test
    @DisplayName("Não deve permitir salvar dois registros com o mesmo ID de referência")
    void naoDevePermitirDuplicidadeDeIdReferencia() {
        String idRefDuplicado = "prod_123_review_1";
        repository.save(criarAnalisePadrao(idRefDuplicado, "Primeira avaliação"));
        entityManager.flush();

        AnaliseSentimento duplicata = criarAnalisePadrao(idRefDuplicado, "Segunda avaliação (falha)");

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            repository.save(duplicata);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Deve atualizar o texto de uma análise existente")
    void deveAtualizarAnalise() {
        AnaliseSentimento original = repository.save(criarAnalisePadrao("prod_1_review_1", "Texto antigo"));
        Long id = original.getId();

        AnaliseSentimento atualizada = new AnaliseSentimento(new TextoAvaliacao("Texto novo editado"), new IdReferencia("prod_1_review_1"));

        AnaliseSentimento managed = repository.findById(id).get();

        Assertions.assertNotNull(managed.getDataProcessamento());
    }

    @Test
    @DisplayName("Não deve criar Objeto com texto inválido (Validação do Construtor)")
    void validacaoDeDominioTexto() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TextoAvaliacao("Oi");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new IdReferencia("id_invalido");
        });
    }

    // --- MÉTODOS AUXILIARES ---

    private AnaliseSentimento criarAnalisePadrao(String idRef, String texto) {
        AnaliseSentimento analise = new AnaliseSentimento(
                new TextoAvaliacao(texto),
                new IdReferencia(idRef)
        );

        analise.registrarResultado(
                TipoSentimento.POSITIVO,
                new Probabilidade(0.95),
                "v1-test",
                LocalDateTime.now()
        );

        return analise;
    }
}
package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
public class AvaliacaoRepositoryTest {

    @Autowired
    private AvaliacaoRepository repository;

    @Test
    public void deveSalvarERecuperarAvaliacaoComSucesso() {
        // 1. Prepara os dados (respeitando as validações das suas classes)
        // O texto deve ter entre 5 e 1000 caracteres
        TextoAvaliacao texto = new TextoAvaliacao("Este produto é muito bom, gostei bastante!");

        // O ID deve seguir o padrão regex prod_X_review_Y
        IdReferencia idRef = new IdReferencia("prod_123_review_456");

        // Cria a entidade
        AnaliseSentimento analise = new AnaliseSentimento(texto, idRef);

        // Simula o resultado que viria da API Python
        analise.registrarResultado(
                TipoSentimento.POSITIVO,
                new Probabilidade(0.95), // Valor entre 0 e 1
                "v1.0-test",
                LocalDateTime.now()
        );

        // 2. Executa a ação de salvar
        AnaliseSentimento salvo = repository.save(analise);

        // 3. Validações
        Assertions.assertNotNull(salvo.getId(), "O ID não deveria ser nulo após salvar");

        // Busca no banco para garantir que persistiu
        Optional<AnaliseSentimento> buscado = repository.findById(salvo.getId());
        Assertions.assertTrue(buscado.isPresent());
        Assertions.assertEquals("prod_123_review_456", buscado.get().getIdReferencia().getValor());

        System.out.println("Teste realizado com sucesso! ID gerado: " + salvo.getId() + "\n" + buscado.get().getIdReferencia().getValor());
    }
}
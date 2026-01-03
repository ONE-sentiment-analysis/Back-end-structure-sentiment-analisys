package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.cache.SentimentCache;
import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.request.ValuationIdData;
import br.com.one.sentiment_analysis.dto.response.SentimentListItemResponse;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.model.AnaliseSentimento;
import br.com.one.sentiment_analysis.repository.AvaliacaoRepository;
import br.com.one.sentiment_analysis.model.IdReferencia;
import br.com.one.sentiment_analysis.service.ExternalApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/sentiment")
@Tag(name = "Endpoint para realizar análise de sentimentos", description = "Retorna probabilidade e acurácia do comentário")
public class SentimentController {
    private static final Logger logger = Logger.getLogger(SentimentController.class.getName());

    @Autowired
    private AvaliacaoRepository repository;

    private final ExternalApiService sentimentService;
    private static final int TAMANHO_PAGINACAO = 12;

    public SentimentController(ExternalApiService sentimentService) {
        this.sentimentService = sentimentService;
    }

    @PostMapping
    @Operation(summary = "Analisar comentário", description = "Recebe um texto e retorna análise de sentimento")
    @ApiResponse(
            responseCode = "200",
            description = "Comentário analisado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"previsao\": \"positivo\", \"probabilidade\": 0.94 }"
                    )
            )
    )
    public ResponseEntity<SentimentResponse> analisarComentario(@RequestBody SentimentAnalysisRequest texto) throws IOException, InterruptedException {
        SentimentResponse response = sentimentService.analisar(texto);
        if (SentimentCache.containsKey(SentimentCache.cacheKeyValue(texto))) {
            logger.info("Comentário encontrado no cache.");
            return ResponseEntity.ok(SentimentCache.get(SentimentCache.cacheKeyValue(texto)));
        }
        logger.info("Comentário analisado com sucesso.");
        SentimentCache.put(SentimentCache.cacheKeyValue(texto), response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Procurar avaliações", description = "Busca avaliações de um produto por ID com paginação")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de avaliações retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"status\": \"SUCESSO\", \"total\": 2, \"itens\": [ { \"id\": 1, \"texto\": \"Ótimo produto\", \"previsao\": \"positivo\", \"probabilidade\": 0.95, \"dataProcessamento\": \"2025-12-23\" } ] }"
                    )
            )
    )

    public ResponseEntity<Page<SentimentListItemResponse>> procurarAvaliacoes(
            ValuationIdData idProduto,
            @PageableDefault(size = TAMANHO_PAGINACAO) Pageable pageable) {

        IdReferencia idProdutoFormatado = new IdReferencia(idProduto.id());

        Page<AnaliseSentimento> pageResult = repository.buscarPorIdProduto(
                idProdutoFormatado
                        .getIdAvaliacaoExtraido()
                        .idProduto(),
                pageable);
                
        Page<SentimentListItemResponse> response = pageResult.map(SentimentListItemResponse::new);
        logger.info("Lista de avaliações retornada com sucesso.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

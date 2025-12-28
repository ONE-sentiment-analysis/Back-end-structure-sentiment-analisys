package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentItemResponse;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.model.AnaliseSentimento;
import br.com.one.sentiment_analysis.model.AvaliacaoRepository;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/sentiment")
@Tag(name = "Endpoint para realizar análise de sentimentos", description = "Retorna probabilidade e acurácia do comentário")
public class SentimentController {

    @Autowired
    private AvaliacaoRepository repository;

    private final ExternalApiService sentimentService;
    private static final int TAMANHO_PAGINACAO = 10;

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
    public ResponseEntity<SentimentResponse> procurarAvaliacoes(
            @RequestParam Long idProduto,
            @PageableDefault(size = TAMANHO_PAGINACAO) Pageable pageable) {

        Page<AnaliseSentimento> page = repository.buscarPorIdProduto(idProduto, pageable);

        List<SentimentItemResponse> itens = page.getContent().stream()
                .map(analise -> new SentimentItemResponse(
                        analise.getIdReferencia().getValor(),
                        analise.getTexto().getValor(),
                        analise.getPrevisao().name(),
                        analise.getProbabilidade().getValor(),
                        analise.getDataProcessamento()
                ))
                .toList();

        SentimentResponse response = new SentimentResponse(
                "SUCESSO",
                (int) page.getTotalElements(),
                itens
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

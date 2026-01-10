package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentListItemResponse;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.model.avaliacao.AnaliseSentimento;
import br.com.one.sentiment_analysis.repository.SentimentRepository;
import br.com.one.sentiment_analysis.service.ExternalApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/v1/sentiment")
@Tag(name = "Endpoint para realizar análise de sentimentos", description = "Retorna probabilidade e acurácia do comentário")
public class SentimentController {
    private static final Logger logger = LoggerFactory.getLogger(SentimentController.class.getName());

    @Autowired
    private SentimentRepository repository;

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
    public ResponseEntity<SentimentResponse> analisarComentario(@RequestBody SentimentAnalysisRequest texto) {
        SentimentResponse response = sentimentService.analisar(texto);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Análise em lote via CSV", description = "Recebe um CSV (colunas: id, texto, versão do modelo) e retorna um CSV com as análises.")
    @ApiResponse(
            responseCode = "200",
            description = "Arquivo processado com sucesso",
            content = @Content(
                    mediaType = "text/csv",
                    examples = @ExampleObject(
                            value = """
                                    ID Referencia,Texto,Previsao,Probabilidade,Versao Modelo,Data Processamento,Status,Detalhe do Erro
                                    prod_1_review_100,Produto excelente,POSITIVO,0.98,v1.5,2025-01-06T10:00:00,SUCESSO,
                                    prod_1_review_101,Nao gostei,NEGATIVO,0.85,v1.5,2025-01-06T10:00:05,SUCESSO,
                                    123,Texto muito curto,,,,ERRO_VALIDACAO,O ID '123' está fora do padrão esperado"""
                    )
            )
    )
    public ResponseEntity<StreamingResponseBody> analisarEmLote(@RequestParam("file") @NotNull @Size(max = 10 * 1024 * 1024) MultipartFile file) {
        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = file.getInputStream()) {
                sentimentService.processarCsv(inputStream, outputStream);
            } catch (IOException e) {
                logger.warn("Download cancelado ou interrompido pelo cliente: {}", e.getMessage());
            }
        };


        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=analise_sentimentos_resultado.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return ResponseEntity.ok()
                .headers(headers)
                .body(stream);
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
            Long idProduto,
            @PageableDefault(size = TAMANHO_PAGINACAO) Pageable pageable) {

        Page<AnaliseSentimento> pageResult = repository.buscarPorIdProduto(idProduto, pageable);
                
        Page<SentimentListItemResponse> response = pageResult.map(SentimentListItemResponse::new);
        logger.info("Lista de avaliações retornada com sucesso.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

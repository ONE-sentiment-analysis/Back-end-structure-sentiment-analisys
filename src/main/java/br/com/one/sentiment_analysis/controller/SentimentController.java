package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentListItemResponse;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.model.avaliacao.AnaliseSentimento;
import br.com.one.sentiment_analysis.model.avaliacao.VersaoModelo;
import br.com.one.sentiment_analysis.repository.SentimentRepository;
import br.com.one.sentiment_analysis.service.ExternalApiService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.tika.Tika;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

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
    public ResponseEntity<SentimentResponse> analisarComentario(@RequestBody SentimentAnalysisRequest dadosRequisicao) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        SentimentResponse response = sentimentService.analisar(dadosRequisicao, email);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Análise em lote via CSV", description = "Recebe um CSV (colunas: texto, versão do modelo) e retorna um CSV com as análises.")
    @ApiResponse(
            responseCode = "200",
            description = "Arquivo processado com sucesso",
            content = @Content(
                    mediaType = "text/csv",
                    examples = @ExampleObject(
                            value = """
                                    Texto,Previsao,Probabilidade,Versao Modelo,Data Processamento,Status,Detalhe do Erro
                                    Produto excelente,POSITIVO,0.98,LR,2025-01-06T10:00:00,SUCESSO,
                                    Nao gostei,NEGATIVO,0.85,NB,2025-01-06T10:00:05,SUCESSO,
                                    Texto muito curto,,,,ERRO_VALIDACAO"""
                    )
            )
    )
    public ResponseEntity<StreamingResponseBody> analisarEmLote(@RequestParam("file") MultipartFile file) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio.");
        }

        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = file.getInputStream()) {
                sentimentService.processarCsv(inputStream, outputStream, email);
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

    @PostMapping(value = "/csv-to-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Converter CSV para Excel Formatado com Análise",
            description = "Recebe um CSV, processa a análise de sentimento via serviço externo e retorna um XLSX estilizado.")
    public ResponseEntity<byte[]> converterCsvParaExcel(@RequestParam("file") MultipartFile file) throws IOException, CsvException {

        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());

        if (!mimeType.contains("text") && !mimeType.contains("csv")) {
            throw new IllegalArgumentException("O arquivo enviado não é um CSV válido. Tipo detectado: " + mimeType);
        }

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        List<String[]> linhasCsv;
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream())) {
            CSVReader csvReader = new CSVReader(reader);
            linhasCsv = csvReader.readAll();
        }

        if (linhasCsv.isEmpty()) {
            throw new IllegalArgumentException("O arquivo CSV está vazio.");
        }

        try (Workbook workbook = new SXSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet("Relatório Analítico");
            sheet.trackAllColumnsForAutoSizing();

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);

            String[] colunasSaida = {
                    "Texto Original", "Previsão", "Probabilidade",
                    "Versão Modelo", "Data Processamento", "Status", "Detalhe do Erro"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < colunasSaida.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunasSaida[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            boolean primeiraLinha = true;

            for (String[] record : linhasCsv) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    if (record.length > 0 && (record[0].toLowerCase().contains("id") || record[1].equalsIgnoreCase("texto")))  {
                        continue;
                    }
                }


                Row row = sheet.createRow(rowNum++);

                String texto = (record.length > 1) ? record[1] : "";
                String modelo = (record.length > 2)
                        ? record[2]
                        : VersaoModelo.LOGISTIC_REGRESSION.getPythonModelName();

                if (texto == null || texto.trim().isEmpty()) {
                    createCell(row, 0, "LINHA VAZIA", dataStyle);
                    createCell(row, 6, "Texto não identificado na coluna 2", dataStyle);
                    continue;
                }

                try {
                    SentimentAnalysisRequest request = new SentimentAnalysisRequest(texto, modelo);
                    SentimentResponse response = sentimentService.analisar(request, email);

                    String status = "indisponível".equals(response.previsao()) ? "AVISO_FALLBACK" : "SUCESSO";
                    String erro = "indisponível".equals(response.previsao()) ? "Serviço instável" : "";

                    createCell(row, 0, response.texto(), dataStyle);
                    createCell(row, 1, response.previsao(), dataStyle);
                    createCell(row, 2, response.probabilidadeFormatada(), dataStyle);
                    createCell(row, 3, response.versaoModelo(), dataStyle);
                    createCell(row, 4, response.dataProcessamento().toString(), dataStyle);
                    createCell(row, 5, status, dataStyle);
                    createCell(row, 6, erro, dataStyle);

                } catch (Exception e) {
                    createCell(row, 0, texto, dataStyle);
                    createCell(row, 5, "ERRO_INTERNO", dataStyle);
                    createCell(row, 6, e.getMessage(), dataStyle);
                }
            }

            for (int i = 0; i < colunasSaida.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_analitico_formatado.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    @GetMapping
    @Operation(summary = "Procurar avaliações", description = "Busca avaliações de um produto por ID com paginação")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de avaliações retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{ \"status\": \"SUCESSO\", " +
                                    "\"total\": 2, \"itens\": [ { \"id\": 1, " +
                                    "\"texto\": \"Ótimo produto\", \"previsao\": \"positivo\", " +
                                    "\"probabilidade\": 0.95, \"dataProcessamento\": \"2025-12-23\" } ] }"
                    )
            )
    )
    public ResponseEntity<Page<SentimentListItemResponse>> procurarAvaliacoes(
            Long idAvaliacao,
            @PageableDefault(size = TAMANHO_PAGINACAO, sort = "dataProcessamento", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AnaliseSentimento> pageResult = repository.findAllById(idAvaliacao, pageable);
                
        Page<SentimentListItemResponse> response = pageResult.map(SentimentListItemResponse::new);
        logger.info("Lista de avaliações retornada com sucesso.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

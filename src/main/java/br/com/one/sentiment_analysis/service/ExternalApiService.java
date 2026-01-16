package br.com.one.sentiment_analysis.service;

import br.com.one.sentiment_analysis.dto.integration.PythonRequestDTO;
import br.com.one.sentiment_analysis.dto.integration.PythonResponseDTO;
import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.exception.UserNotFoundException;
import br.com.one.sentiment_analysis.model.avaliacao.*;
import br.com.one.sentiment_analysis.model.user.Usuario;
import br.com.one.sentiment_analysis.repository.SentimentRepository;
import br.com.one.sentiment_analysis.repository.UsuarioRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class ExternalApiService {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiService.class);

    private final IExternalApiService externalApiService;
    private final SentimentRepository repository;
    private final Counter fallBackCounter;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    @Lazy
    private ExternalApiService self;

    public ExternalApiService(IExternalApiService externalApiService,
                              SentimentRepository repository,
                              UsuarioRepository usuarioRepository,
                              MeterRegistry registry) {
        this.externalApiService = externalApiService;
        this.usuarioRepository = usuarioRepository;
        this.repository = repository;

        this.fallBackCounter = Counter.builder("external_api_fallback_total")
                .description("Número de vezes que o fallback da API externa foi acionado.")
                .register(registry);
    }

    @Transactional
    @CircuitBreaker(name = "PythonApiCircuitBreaker", fallbackMethod = "fallbackAnalisar")
    @Retry(name = "PythonApiRetry")
    @Bulkhead(name = "PythonApiBulkhead")
    public SentimentResponse analisar(SentimentAnalysisRequest request, String usuarioEmail) {

        log.info("Iniciando análise");

        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        var entidade = new AnaliseSentimento(
                new TextoAvaliacao(request.text()),
                VersaoModelo.fromString(request.model())
        );

        PythonRequestDTO pythonRequest = new PythonRequestDTO(request.text(), request.model());

        PythonResponseDTO pythonResponse = externalApiService.analisar(pythonRequest);

        TipoSentimento sentimentoValidacao = getTipoSentimento(pythonResponse);

        entidade.registrarResultado(
                sentimentoValidacao,
                new Probabilidade(pythonResponse.probability()),
                LocalDateTime.now()
        );

        usuario.adicionarAvaliacao(entidade);

        usuarioRepository.save(usuario);

        log.info("Análise de sentimento concluída com sucesso");

        Probabilidade probabilidade = new Probabilidade(pythonResponse.probability());
        String probabilidadeFormatada = probabilidade.asPercentual();

        return new SentimentResponse(
                request.text(),
                sentimentoValidacao.name(),
                probabilidadeFormatada,
                pythonResponse.modelVersion().toUpperCase(),
                entidade.getDataProcessamento()
        );
    }

    private static @NonNull TipoSentimento getTipoSentimento(PythonResponseDTO pythonResponse) {
        String sentimento = pythonResponse.sentiment().toUpperCase();

        double probabilidade = pythonResponse.probability();

        TipoSentimento sentimentoConvertido;

        if (probabilidade < 0.7) {
            sentimentoConvertido = TipoSentimento.NEUTRO;
        } else if ("POSITIVO".equals(sentimento)) {
            sentimentoConvertido = TipoSentimento.POSITIVO;
        } else if ("NEGATIVO".equals(sentimento)) {
            sentimentoConvertido = TipoSentimento.NEGATIVO;
        } else {
            sentimentoConvertido = TipoSentimento.NEUTRO;
        }
        return sentimentoConvertido;
    }

    public void processarCsv(InputStream inputStream, OutputStream outputStream, String email) {
        try {
            outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            try (
                    CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                String[] header = {
                        "Texto", "Previsao", "Probabilidade",
                        "Versao Modelo", "Data Processamento", "Status", "Detalhe do Erro"
                };
                writer.writeNext(header);
                writer.flush();

                try {
                    String[] nextLine;
                    csvReader.readNext();

                    while ((nextLine = csvReader.readNext()) != null) {
                        if (nextLine.length < 2) {
                            writer.writeNext(new String[]{"ERRO_FORMATO", "Linha mal formatada ou vazia"});
                            continue;
                        }

                        String texto = nextLine[1];
                        String modelVersion = (nextLine.length > 2)
                                ? nextLine[2]
                                : VersaoModelo.LOGISTIC_REGRESSION.getPythonModelName();

                        processarLinha(writer, texto, modelVersion, email);
                    }
                } catch (Exception e) {
                    log.error("Erro fatal durante o processamento do CSV", e);

                    String[] erroFatal = {
                            "SISTEMA", "N/A", "", "", "", "",
                            "ERRO_CRITICO",
                            "Processamento interrompido: " + e.getMessage()
                    };
                    writer.writeNext(erroFatal);
                }
            }
        } catch (IOException e) {
            log.error("Erro de I/O irrecuperável no streaming", e);
        }
    }

    private void processarLinha(CSVWriter writer, String texto, String modelVersion, String email) {
        try {
            SentimentAnalysisRequest request = new SentimentAnalysisRequest(texto, modelVersion);

            SentimentResponse response = self.analisar(request, email);

            String status = "indisponível".equals(response.previsao()) ? "AVISO_FALLBACK" : "SUCESSO";
            String msgErro = "indisponível".equals(response.previsao()) ? "Serviço externo instável, retornado padrão." : "";

            writer.writeNext(new String[] {
                    response.texto(),
                    response.previsao(),
                    response.probabilidadeFormatada(),
                    response.versaoModelo(),
                    response.dataProcessamento().toString(),
                    status,
                    msgErro
            });

        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação CSV: {}", e.getMessage());
            writer.writeNext(new String[] {
                    texto, "", "", "", "", "ERRO_VALIDACAO", e.getMessage()
            });

        } catch (Exception e) {
            log.error("Erro inesperado processando: ", e);
            writer.writeNext(new String[] {
                    texto, "", "", "", "", "ERRO_INTERNO", "Erro inesperado: " + e
            });
        }
    }

    public void processarParaExcel(InputStream inputStream, OutputStream outputStream, String email) {
        try (
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                CSVReader csvReader = new CSVReader(reader);

                Workbook workbook = new SXSSFWorkbook()
        ) {
            Sheet sheet = workbook.createSheet("Análise de Sentimentos");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);

            String[] headers = {
                    "Texto Original", "Previsão", "Probabilidade",
                    "Versão Modelo", "Data Processamento", "Status", "Detalhe do Erro"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            String[] nextLine;
            csvReader.readNext();

            int rowNum = 1;
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length < 2) continue;

                String texto = nextLine[1];
                String modelVersion = (nextLine.length > 2)
                        ? nextLine[2]
                        : VersaoModelo.LOGISTIC_REGRESSION.getPythonModelName();

                Row row = sheet.createRow(rowNum++);
                try {
                    SentimentAnalysisRequest request = new SentimentAnalysisRequest(texto, modelVersion);
                    SentimentResponse response = self.analisar(request, email);

                    String status = "indisponível".equals(response.previsao()) ? "AVISO_FALLBACK" : "SUCESSO";
                    String msgErro = "indisponível".equals(response.previsao()) ? "Serviço instável" : "";

                    row.createCell(0).setCellValue(response.texto());
                    row.createCell(1).setCellValue(response.previsao());
                    row.createCell(2).setCellValue(response.probabilidadeFormatada());
                    row.createCell(3).setCellValue(response.versaoModelo());
                    row.createCell(4).setCellValue(response.dataProcessamento().toString());
                    row.createCell(5).setCellValue(status);
                    row.createCell(6).setCellValue(msgErro);

                } catch (Exception e) {
                    row.createCell(0).setCellValue(texto);
                    row.createCell(5).setCellValue("ERRO");
                    row.createCell(6).setCellValue(e.getMessage());
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

        } catch (IOException | CsvValidationException e) {
            log.error("Erro ao gerar Excel", e);
            throw new RuntimeException("Erro ao gerar arquivo Excel", e);
        }
    }

    public SentimentResponse fallbackAnalisar(SentimentAnalysisRequest request, String usuarioEmail, Throwable t) {
        log.error("Fallback executado no Circuit Breaker na análise de sentimento | usuario={} | erro={}", usuarioEmail, t.getMessage());

        fallBackCounter.increment();

        return new SentimentResponse(
                request.text(),
                "indisponível",
                "0%",
                "indisponível",
                LocalDateTime.now()
        );
    }
}
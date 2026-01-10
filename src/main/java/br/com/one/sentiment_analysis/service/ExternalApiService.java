package br.com.one.sentiment_analysis.service;

import br.com.one.sentiment_analysis.dto.integration.PythonRequestDTO;
import br.com.one.sentiment_analysis.dto.integration.PythonResponseDTO;
import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.model.avaliacao.*;
import br.com.one.sentiment_analysis.repository.SentimentRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

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


    public ExternalApiService(IExternalApiService externalApiService,
                              SentimentRepository repository,
                              MeterRegistry registry) {
        this.externalApiService = externalApiService;
        this.repository = repository;

        this.fallBackCounter = Counter.builder("external_api_fallback_total")
                .description("Número de vezes que o fallback da API externa foi acionado.")
                .register(registry);
    }

    @Transactional
    @CircuitBreaker(name = "PythonApiCircuitBreaker", fallbackMethod = "fallbackAnalisar")
    @Retry(name = "PythonApiRetry")
    @Bulkhead(name = "PythonApiBulkhead")
    public SentimentResponse analisar(SentimentAnalysisRequest request) {

        log.info("Iniciando análise para o ID: {}", request.id());

        PythonRequestDTO pythonRequest = new PythonRequestDTO(request.text(), request.model());

        PythonResponseDTO pythonResponse = externalApiService.analisar(pythonRequest);

        var entidade = new AnaliseSentimento(
                new TextoAvaliacao(request.text()),
                new IdReferencia(request.id())
        );

        entidade.registrarResultado(
                TipoSentimento.valueOf(pythonResponse.sentiment().toUpperCase()),
                new Probabilidade(pythonResponse.probability()),
                pythonResponse.modelVersion(),
                LocalDateTime.now()
        );

        repository.saveAndFlush(entidade);

        log.info("Análise de sentimento concluída com sucesso para ID: {}", request.id());

        Probabilidade probabilidade = new Probabilidade(pythonResponse.probability());
        String probabilidadeFormatada = probabilidade.asPercentual();

        return new SentimentResponse(
                request.id(),
                request.text(),
                pythonResponse.sentiment(),
                probabilidadeFormatada,
                pythonResponse.modelVersion(),
                entidade.getDataProcessamento()
        );
    }

    public void processarCsv(InputStream inputStream, OutputStream outputStream) {
        try (
                CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                CSVReader csvReader = new CSVReader(reader)
        ) {
            String[] header = {
                    "ID Referencia", "Texto", "Previsao", "Probabilidade",
                    "Versao Modelo", "Data Processamento", "Status", "Detalhe do Erro"
            };
            writer.writeNext(header);
            writer.flush();

            try{
                String[] nextLine;
                csvReader.readNext();

                while ((nextLine = csvReader.readNext()) != null) {
                    if (nextLine.length < 2) {
                        writer.writeNext(new String[] {"", "", "", "", "", "", "ERRO_FORMATO", "Linha mal formatada ou vazia"});
                        continue;
                    }

                    String id = nextLine[0];
                    String texto = nextLine[1];
                    String  modelVersion = nextLine[2];

                    processarLinha(writer, id, texto, modelVersion);
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

        } catch (IOException e) {
            log.error("Erro de I/O irrecuperável no streaming", e);
        }
    }

    private void processarLinha(CSVWriter writer, String id, String texto, String modelVersion) {
        try {
            SentimentAnalysisRequest request = new SentimentAnalysisRequest(id, texto, modelVersion);

            SentimentResponse response = this.analisar(request);

            String status = "indisponível".equals(response.previsao()) ? "AVISO_FALLBACK" : "SUCESSO";
            String msgErro = "indisponível".equals(response.previsao()) ? "Serviço externo instável, retornado padrão." : "";

            writer.writeNext(new String[] {
                    response.idReferencia(),
                    response.texto(),
                    response.previsao(),
                    response.probabilidadeFormatada(),
                    response.versaoModelo(),
                    response.dataProcessamento().toString(),
                    status,
                    msgErro
            });

        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação CSV linha ID {}: {}", id, e.getMessage());
            writer.writeNext(new String[] {
                    id, texto, "", "", "", "", "ERRO_VALIDACAO", e.getMessage()
            });

        } catch (Exception e) {
            log.error("Erro inesperado processando ID {}", id, e);
            writer.writeNext(new String[] {
                    id, texto, "", "", "", "", "ERRO_INTERNO", "Erro inesperado: " + e
            });
        }
    }

    public SentimentResponse fallbackAnalisar(SentimentAnalysisRequest request, Throwable t) {
        log.error("Fallback executado no Circuit Breaker da análise de sentimento para ID={} | erro={}",
        request.id(), t.getMessage());

        fallBackCounter.increment();

        return new SentimentResponse(
                request.id(),
                request.text(),
                "indisponível",
                "0%",
                "indisponível",
                LocalDateTime.now()
        );
    }
}
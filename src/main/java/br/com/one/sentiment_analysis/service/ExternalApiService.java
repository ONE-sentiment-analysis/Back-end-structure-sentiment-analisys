package br.com.one.sentiment_analysis.service;

import br.com.one.sentiment_analysis.dto.integration.PythonRequestDTO;
import br.com.one.sentiment_analysis.dto.integration.PythonResponseDTO;
import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.model.*;
import br.com.one.sentiment_analysis.repository.AvaliacaoRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class ExternalApiService {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiService.class);

    private final IExternalApiService externalApiService;
    private final AvaliacaoRepository repository;
    private final Counter fallBackCounter;

    public ExternalApiService(IExternalApiService externalApiService,
                              AvaliacaoRepository repository,
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

        PythonRequestDTO pythonRequest = new PythonRequestDTO(request.text());

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

        return new SentimentResponse(
                request.id(),
                request.text(),
                pythonResponse.sentiment(),
                pythonResponse.probability(),
                pythonResponse.modelVersion(),
                entidade.getDataProcessamento()
        );
    }

    public SentimentResponse fallbackAnalisar(SentimentAnalysisRequest request, Throwable t) {
        log.error("Fallback executado no Circuit Breaker da análise de sentimento para ID={} | erro={}",
        request.id(), t.getMessage());

        fallBackCounter.increment();

        return new SentimentResponse(
                request.id(),
                request.text(),
                "indisponível",
                0.0,
                "indisponível",
                LocalDateTime.now()
        );
    }
}
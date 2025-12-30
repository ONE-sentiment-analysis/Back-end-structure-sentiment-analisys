package br.com.one.sentiment_analysis.service;

import br.com.one.sentiment_analysis.dto.integration.PythonRequestDTO;
import br.com.one.sentiment_analysis.dto.integration.PythonResponseDTO;
import br.com.one.sentiment_analysis.dto.integration.ReviewItemDTO;
import br.com.one.sentiment_analysis.dto.request.ReviewRequestItem;
import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentItemResponse;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.exception.ExternalApiException;
import br.com.one.sentiment_analysis.model.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.prometheus.metrics.core.metrics.Counter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExternalApiService {

    private static final Logger log =
            LoggerFactory.getLogger(ExternalApiService.class);

    private final IExternalApiService externalApiService;
    private final AvaliacaoRepository repository;
    private final Counter fallBackCounter;
    public ExternalApiService(IExternalApiService externalApiService,
                              AvaliacaoRepository repository) {
        this.externalApiService = externalApiService;
        this.repository = repository;

        this.fallBackCounter = Counter.builder()
                .name("external_api_fallback_total")
                .help("Número de vezes que o fallback da API externa foi acionado.")
                .register();
    }

    
    @Transactional
    @CircuitBreaker(
        name = "PythonApiCircuitBreaker",
        fallbackMethod = "fallbackAnalisar"
    )
    public SentimentResponse analisar(SentimentAnalysisRequest request) {

        PythonRequestDTO pythonRequest = new PythonRequestDTO(
                "req_" + UUID.randomUUID(),
                request.reviews().stream()
                        .map(r -> new ReviewItemDTO(r.id(), r.text()))
                        .toList()
        );

        PythonResponseDTO pythonResponse = externalApiService.analisar(pythonRequest);

        Map<String, String> textoMap = request.reviews().stream()
                .collect(Collectors.toMap(
                        ReviewRequestItem::id,
                        ReviewRequestItem::text
                ));

        var entidades = pythonResponse.results().stream()
                .map(res -> {
                    var entidade = new AnaliseSentimento(
                            new TextoAvaliacao(textoMap.get(res.id())),
                            new IdReferencia(res.id())
                    );

                    entidade.registrarResultado(
                            TipoSentimento.valueOf(res.sentiment().toUpperCase()),
                            new Probabilidade(res.probability()),
                            pythonResponse.modelVersion(),
                            LocalDateTime.now()
                    );

                    return entidade;
                })
                .toList();

        repository.saveAllAndFlush(entidades);

        var resposta = pythonResponse.results().stream()
                .map(res -> new SentimentItemResponse(
                        res.id(),
                        textoMap.get(res.id()),
                        res.sentiment(),
                        res.probability(),
                        LocalDateTime.now()
                ))
                .toList();

        return new SentimentResponse("SUCESSO", resposta.size(), resposta);
    }

    public SentimentResponse fallbackAnalisar(SentimentAnalysisRequest request,Throwable t) {
        log.error("Fallback executado no Circuit Breaker da análise de sentimento | reviewsCount={} | erro={}",request.reviews().size(),t.getMessage(),t);
        
        // Métrica 
        fallBackCounter.inc();
        
        throw new ExternalApiException("Serviço de análise de sentimento indisponível no momento.",t);
        
}

}

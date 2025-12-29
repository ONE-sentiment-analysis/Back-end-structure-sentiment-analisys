package br.com.one.sentiment_analysis.service;

import br.com.one.sentiment_analysis.dto.integration.PythonRequestDTO;
import br.com.one.sentiment_analysis.dto.integration.PythonResponseDTO;
import br.com.one.sentiment_analysis.dto.integration.ReviewItemDTO;
import br.com.one.sentiment_analysis.dto.request.ReviewRequestItem;
import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentItemResponse;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.exception.ExternalApiException;
import br.com.one.sentiment_analysis.model.AnaliseSentimento;
import br.com.one.sentiment_analysis.model.AvaliacaoRepository;
import br.com.one.sentiment_analysis.model.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ExternalApiService {

    // Todo: Precisa trocar URL API pelo url que vem da api com python com modelo no application.properties
    @Value("${api.python.url}")
    private String URL_API;

    private final ObjectMapper mapper;
    private final AvaliacaoRepository repository;
    private final HttpClient client = HttpClient.newHttpClient();

    public ExternalApiService(ObjectMapper mapper, AvaliacaoRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    // POST
    @Transactional
    public SentimentResponse analisar(SentimentAnalysisRequest sentimentRequest) throws IOException, InterruptedException { // 1. Renomeei o parâmetro para evitar conflito

        List<ReviewItemDTO> pythonReviews = sentimentRequest.reviews().stream()
                .map(r -> new ReviewItemDTO(r.id(), r.text()))
                .toList();

        PythonRequestDTO pythonRequest = new PythonRequestDTO("req_" + UUID.randomUUID(), pythonReviews);
        String jsonRequest = mapper.writeValueAsString(pythonRequest);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_API))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
                Logger.getLogger(ExternalApiService.class.getName()).severe("Erro na API Python: " + httpResponse.statusCode());    
                throw new ExternalApiException("Erro na API Python: " + httpResponse.statusCode());
        }

        PythonResponseDTO pythonResponse = mapper.readValue(httpResponse.body(), PythonResponseDTO.class);
        
        Map<String, String> textoMap = sentimentRequest.reviews().stream()
        .collect(Collectors.toMap(ReviewRequestItem::id, ReviewRequestItem::text));
        
        List<AnaliseSentimento> entidadesParaSalvar = new ArrayList<>();
        
        for (var resultado : pythonResponse.results()) {
            String textoOriginal = textoMap.get(resultado.id());

            AnaliseSentimento entidade = new AnaliseSentimento(
                    new TextoAvaliacao(textoOriginal),
                    new IdReferencia(resultado.id())
            );

            entidade.registrarResultado(
                    TipoSentimento.valueOf(resultado.sentiment().toUpperCase()),
                    new Probabilidade(resultado.probability()),
                    pythonResponse.modelVersion(),
                    LocalDateTime.now()
            );
            
            entidadesParaSalvar.add(entidade);
        }
        
        repository.saveAll(entidadesParaSalvar);

        List<SentimentItemResponse> itensResposta = pythonResponse.results().stream()
                .map(res -> new SentimentItemResponse(
                        res.id(),
                        textoMap.get(res.id()),
                        res.sentiment(),
                        res.probability(),
                        LocalDateTime.now()
                )).toList();

        Logger.getLogger(ExternalApiService.class.getName()).info("Requisição enviada para API Python.");
        return new SentimentResponse("SUCESSO", itensResposta.size(), itensResposta);
    }
}

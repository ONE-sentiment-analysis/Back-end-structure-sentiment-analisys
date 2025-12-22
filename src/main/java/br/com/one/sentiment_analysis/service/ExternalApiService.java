package br.com.one.sentiment_analysis.service;

import br.com.one.sentiment_analysis.dto.integration.PythonRequestDTO;
import br.com.one.sentiment_analysis.dto.integration.PythonResponseDTO;
import br.com.one.sentiment_analysis.dto.integration.ReviewItemDTO;
import br.com.one.sentiment_analysis.dto.request.ReviewRequestItem;
import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentItemResponse;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.exception.ExternalApiException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExternalApiService {

    // Todo: trocar URL API pelo url que vem da api com python com modelo
    private final String URL_API = "https://jsonplaceholder.typicode.com/posts/1";

    ObjectMapper mapper = new ObjectMapper();

    private final HttpClient client = HttpClient.newHttpClient();

    // POST
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
            throw new ExternalApiException("Erro na API Python: " + httpResponse.statusCode());
        }

        PythonResponseDTO pythonResponse = mapper.readValue(httpResponse.body(), PythonResponseDTO.class);

        Map<String, String> textoMap = sentimentRequest.reviews().stream()
                .collect(Collectors.toMap(ReviewRequestItem::id, ReviewRequestItem::text));

        List<SentimentItemResponse> itensResposta = pythonResponse.results().stream()
                .map(res -> new SentimentItemResponse(
                        res.id(),
                        textoMap.get(res.id()),
                        res.sentiment(),
                        res.probability(),
                        LocalDateTime.now()
                )).toList();

        return new SentimentResponse("SUCESSO", itensResposta.size(), itensResposta);
    }
}

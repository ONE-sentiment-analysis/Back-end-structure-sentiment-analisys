package br.com.one.sentiment_analisys.service;

import br.com.one.sentiment_analisys.exception.ExternalApiException;
import br.com.one.sentiment_analisys.model.SentimentRequestModel;
import br.com.one.sentiment_analisys.model.SentimentResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExternalApiService {

    // Todo: trocar URL API pelo url que vem da api com python com modelo
    private final String URL_API = "https://jsonplaceholder.typicode.com/posts/1";
    ObjectMapper mapper = new ObjectMapper();

    // POST
    public SentimentResponse analisar (SentimentRequestModel texto) throws IOException, InterruptedException {

        String json = """
                       {
                            "text": "%s"
                       }
                       """.formatted(texto);

        //Cria o cliente
        HttpClient client = HttpClient.newHttpClient();

        //Cria a requeste;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL_API))
                            .headers("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json)) // json
                            .build();

        //Cria a response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        System.out.printf("Status: %s", response.statusCode());
        //Transforma json em Objeto Java
        return mapper.readValue(response.body(), SentimentResponse.class);
    }


    // Todo: adicionar parâmentro de texto para modelo analisar
    public String getData()  {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(URL_API))
                                .build();

            // TODO:  adicionar Mapper para transformar Json em Objeto Java
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ExternalApiException("Erro ao chamar API externa" + response.statusCode());
            }
            return response.body();

        } catch (IOException | InterruptedException e) {
            throw  new ExternalApiException("Falha ao chamar API externa" + e);
        }
    }
}

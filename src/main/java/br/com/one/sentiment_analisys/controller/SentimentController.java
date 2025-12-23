package br.com.one.sentiment_analisys.controller;

import br.com.one.sentiment_analisys.model.SentimentRequestModel;
import br.com.one.sentiment_analisys.model.SentimentResponse;
import br.com.one.sentiment_analisys.service.ExternalApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/sentiment")
@Tag(name = "Endpoint para realizar análise de sentimentos", description = "Retorna probabilidade e acurácia do comentário")
public class SentimentController {

    private final ExternalApiService sentimentService;

    public SentimentController(ExternalApiService sentimentService) {
        this.sentimentService = sentimentService;
    }


    @PostMapping @Operation(summary = "Analisar comentário", description = "Recebe um texto e retorna análise de sentimento")
    @ApiResponse( responseCode = "200", description = "Comentário analisado com sucesso",
            content = @Content( mediaType = "application/json",
                    examples = @ExampleObject( value = "{ \"previsao\": \"positivo\", \"probabilidade\": 0.94 }" ) ) )
    public SentimentResponse analisarComentario (@RequestBody SentimentRequestModel text)
            throws IOException, InterruptedException {

        return sentimentService.analisar(text);
    }
}

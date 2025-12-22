package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
import br.com.one.sentiment_analysis.dto.response.SentimentItemResponse;
import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
import br.com.one.sentiment_analysis.model.AnaliseSentimento;
import br.com.one.sentiment_analysis.model.AvaliacaoRepository;
import br.com.one.sentiment_analysis.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sentiment")
public class SentimentController {

    @Autowired
    private AvaliacaoRepository repository;

    private final ExternalApiService sentimentService;
    private final Integer tamanhoPaginacao = 10;

    public SentimentController(ExternalApiService sentimentService) {
        this.sentimentService = sentimentService;
    }


    @PostMapping
    public SentimentResponse analisarComentario (@RequestBody SentimentAnalysisRequest texto)
            throws IOException, InterruptedException {
        return sentimentService.analisar(texto);
    }

    @GetMapping
    public ResponseEntity<SentimentResponse> procurarAvaliacoes(
            @RequestParam Long idProduto,
            @PageableDefault(size = tamanhoPaginacao) Pageable pageable) {

        Page<AnaliseSentimento> page = repository.buscarPorIdProduto(idProduto, pageable);

        List<SentimentItemResponse> itens = page.getContent().stream()
                .map(analise -> new SentimentItemResponse(
                        analise.getIdReferencia().getValor(),
                        analise.getTexto().getValor(),
                        analise.getPrevisao().name(),
                        analise.getProbabilidade().getValor(),
                        analise.getDataProcessamento()
                ))
                .toList();

        SentimentResponse response = new SentimentResponse(
                "SUCESSO",
                (int) page.getTotalElements(),
                itens
        );

        return ResponseEntity.ok(response);
    }
}

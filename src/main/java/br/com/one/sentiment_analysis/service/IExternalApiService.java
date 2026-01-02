package br.com.one.sentiment_analysis.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.one.sentiment_analysis.dto.integration.PythonRequestDTO;
import br.com.one.sentiment_analysis.dto.integration.PythonResponseDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(
    name = "PythonModelApi",
    url = "${http://localhost:3000}" // URL do serviço externo configurada no application.properties
)
public interface IExternalApiService {

    @PostMapping(
        value = "/analisar",
        consumes = "application/json" // Especifica que o endpoint consome JSON
    )
    // INFO : alterar o Tipo de retorno se for necessário
    PythonResponseDTO analisar(@RequestBody PythonRequestDTO request);

}

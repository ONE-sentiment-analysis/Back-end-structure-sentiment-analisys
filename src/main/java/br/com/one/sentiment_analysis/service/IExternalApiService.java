package br.com.one.sentiment_analysis.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.one.sentiment_analysis.dto.integration.PythonRequestDTO;
import br.com.one.sentiment_analysis.dto.integration.PythonResponseDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(
    name = "PythonModelApi",
    url = "${api.python.url}"
)
public interface IExternalApiService {

    @PostMapping(
        value = "/analisar",
        consumes = "application/json"
    )
    PythonResponseDTO analisar(@RequestBody PythonRequestDTO request);

}




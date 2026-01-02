package br.com.one.sentiment_analysis.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "JsonApiClient",
    url = "https://jsonplaceholder.typicode.com"

)
public interface IJsonApi {

    @GetMapping("/posts/{id}")
    PostDTO getPostById(@PathVariable("id") Long id);
}

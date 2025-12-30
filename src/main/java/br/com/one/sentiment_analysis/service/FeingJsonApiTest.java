package br.com.one.sentiment_analysis.service;

import org.springframework.stereotype.Service;

@Service
public class FeingJsonApiTest {

    private final IJsonApi jsonApi;

    // Constructor Injection
    public FeingJsonApiTest(IJsonApi jsonApi) {
        this.jsonApi = jsonApi;
    }

    public PostDTO getPostById(Long id) {
        return jsonApi.getPostById(id);
    }
}

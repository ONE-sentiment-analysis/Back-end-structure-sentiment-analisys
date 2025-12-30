package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.service.FeingJsonApiTest;

import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/feing")
public class FeingTestController {

    private final FeingJsonApiTest service;
    private final Logger logger = Logger.getLogger(FeingTestController.class.getName());
    public FeingTestController(FeingJsonApiTest service) {
        this.service = service;
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable long id) {
        var post = service.getPostById(id);
        logger.info("Fetched post: " + post);
        return ResponseEntity.ok(post);
    }
}

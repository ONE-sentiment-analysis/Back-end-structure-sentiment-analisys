package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateAndDeleteUser() {
        User user = new User("pedro");

        ResponseEntity<String> create =
                restTemplate.postForEntity("/api/v1/auth", user, String.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());

        ResponseEntity<Void> delete =
                restTemplate.exchange(
                        "/api/v1/auth/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        user.getId()
                );

        assertEquals(HttpStatus.NO_CONTENT, delete.getStatusCode());
    }
}

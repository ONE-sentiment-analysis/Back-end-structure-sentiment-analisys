package br.com.one.sentiment_analysis.controller;

import br.com.one.sentiment_analysis.DTO.response.PessoaCadastroResponse;
import br.com.one.sentiment_analysis.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateAndDeleteUser() {
        User user = new User("pedro", "pedro@email.com", "senha123");

        ResponseEntity<PessoaCadastroResponse> createResponse =
                restTemplate.postForEntity("/api/v1/auth", user, PessoaCadastroResponse.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        Long idGerado = createResponse.getBody().id();

        ResponseEntity<Void> delete =
                restTemplate.exchange(
                        "/api/v1/auth/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        idGerado
                );

        assertEquals(HttpStatus.NO_CONTENT, delete.getStatusCode());
    }
}

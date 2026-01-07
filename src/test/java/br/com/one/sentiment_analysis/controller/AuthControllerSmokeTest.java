package br.com.one.sentiment_analysis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerSmokeTest {

    @Autowired
    private AuthController authController;

    @Test
    void controllerShouldNotBeNull() {
        assertNotNull(authController);
    }
}

package br.com.one.sentiment_analysis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenUserNotExists() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}

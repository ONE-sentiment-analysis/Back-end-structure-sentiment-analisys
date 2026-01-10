package br.com.one.sentiment_analysis.model.avaliacao;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TextoAvaliacaoTest {

    @Test
    void shouldReturnValidText(){
        TextoAvaliacao texto = new TextoAvaliacao("Teste feito com Junit");
        assertEquals("Teste feito com Junit", texto.getValor());
        assertEquals("Teste feito com Junit", texto.toString());
    }

    @Test
    void shouldReturnExceptionWhenTextIsNull(){
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new TextoAvaliacao(null));
        assertEquals("O texto não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    void shouldRetunExceptionWhenTextIsEmpty(){
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new TextoAvaliacao(" "));
        assertEquals("O texto não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenTextIsShort(){
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> new TextoAvaliacao("Test"));
        assertEquals("O texto precisa atingir o mínimo de 5 caracteres.", ex.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenTextIsToLong(){
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new TextoAvaliacao("a".repeat(1001)));
        assertEquals("O texto excede o limite de 1000 caracteres.", ex.getMessage());
    }
}

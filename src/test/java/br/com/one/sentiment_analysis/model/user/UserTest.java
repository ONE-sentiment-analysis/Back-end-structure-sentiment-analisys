package br.com.one.sentiment_analysis.model.user;

import br.com.one.sentiment_analysis.model.avaliacao.AnaliseSentimento;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve instanciar usuário corretamente usando o construtor com argumentos")
    void user_cenario1() {
        User user = new User("João Silva", "joao@email.com", "senha123");

        assertNull(user.getId());
        assertEquals("João Silva", user.getNome());
        assertEquals("joao@email.com", user.getEmail());
        assertEquals("senha123", user.getSenha());
        assertNotNull(user.getAvaliacoes());
        assertTrue(user.getAvaliacoes().isEmpty());
    }

    @Test
    @DisplayName("Deve instanciar usuário corretamente usando construtor vazio e setters")
    void user_cenario2() {
        User user = new User();
        user.setNome("Maria");
        user.setEmail("maria@email.com");
        user.setSenha("123456");

        assertEquals("Maria", user.getNome());
        assertEquals("maria@email.com", user.getEmail());
        assertEquals("123456", user.getSenha());
    }

    @Test
    @DisplayName("Deve adicionar uma avaliação à lista do usuário")
    void user_cenario3() {
        User user = new User("Teste", "teste@email.com", "123");

        AnaliseSentimento analiseMock = Mockito.mock(AnaliseSentimento.class);

        user.adicionarAvaliacao(analiseMock);

        assertEquals(1, user.getAvaliacoes().size());
        assertEquals(analiseMock, user.getAvaliacoes().getFirst());
    }

    @Test
    @DisplayName("Deve falhar validação quando o nome estiver em branco ou nulo")
    void user_cenario4() {
        User userVazio = new User("", "email@valido.com", "senha123");
        User userNulo = new User(null, "email@valido.com", "senha123");

        Set<ConstraintViolation<User>> violationsVazio = validator.validate(userVazio);
        Set<ConstraintViolation<User>> violationsNulo = validator.validate(userNulo);

        assertFalse(violationsVazio.isEmpty(), "Deveria ter erro de validação para nome vazio");
        assertTrue(violationsVazio.stream().anyMatch(v -> v.getMessage().equals("O nome é obrigatório")));
        assertFalse(violationsNulo.isEmpty(), "Deveria ter erro de validação para nome nulo");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "email-sem-arroba", "teste@", "teste.com", "@dominio.com", "usuario@.com"})
    @DisplayName("Deve falhar validação quando o e-mail for inválido (vazio ou formato incorreto)")
    void user_cenario5(String emailInvalido) {
        User user = new User("Nome Válido", emailInvalido, "senha123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Deveria haver erro de validação para o email: " + emailInvalido);

        boolean erroNoEmail = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));

        assertTrue(erroNoEmail, "O erro de validação deveria ser no campo 'email'");

        boolean mensagemEsperada = violations.stream()
                .anyMatch(v -> v.getMessage().equals("O e-mail é obrigatório") ||
                        v.getMessage().equals("Formato de e-mail inválido"));

        assertTrue(mensagemEsperada, "Mensagem de erro inesperada: " + violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando a senha estiver em branco")
    void user_cenario6() {
        User user = new User("Nome", "teste@email.com", "   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("A senha é obrigatória")));
    }

    @Test
    @DisplayName("Deve passar na validação com todos os dados corretos")
    void user_cenario7() {
        User user = new User("Carlos", "carlos@dominio.com", "segredo123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Não deveria haver erros de validação");
    }

    @Test
    @DisplayName("Deve impedir adição de avaliação nula lançando exceção")
    void user_cenario8() {
        User user = new User("Teste", "teste@email.com", "123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> user.adicionarAvaliacao(null));

        assertEquals("A avaliação não pode ser nula", exception.getMessage());
        assertTrue(user.getAvaliacoes().isEmpty(), "A lista deve permanecer vazia após a tentativa falha");
    }
}
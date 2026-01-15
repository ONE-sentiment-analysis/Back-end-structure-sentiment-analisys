package br.com.one.sentiment_analysis.model.user;

import br.com.one.sentiment_analysis.model.avaliacao.AnaliseSentimento;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    @DisplayName("Deve passar na validação com todos os dados corretos")
    void user_cenario1() {
        User user = new User("Carlos", "carlos@dominio.com", "segredo123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Não deveria haver erros de validação");
    }

    @Test
    @DisplayName("Deve instanciar usuário corretamente usando o construtor com argumentos")
    void user_cenario2() {
        User user = new User("João Silva", "joao@email.com", "senha123");

        assertAll("Estado do User",
                () -> assertNull(user.getId(), "ID deve ser nulo antes da persistência"),
                () -> assertEquals("João Silva", user.getNome()),
                () -> assertEquals("joao@email.com", user.getEmail()),
                () -> assertEquals("senha123", user.getSenha()),
                () -> assertNotNull(user.getAvaliacoes()),
                () -> assertTrue(user.getAvaliacoes().isEmpty())
        );
    }

    @Test
    @DisplayName("Deve instanciar usuário corretamente usando construtor vazio e setters")
    void user_cenario3() {
        User user = new User();
        user.setNome("Maria");
        user.setEmail("maria@email.com");
        user.setSenha("123456");

        assertAll("Validação de Setters",
                () -> assertEquals("Maria", user.getNome()),
                () -> assertEquals("maria@email.com", user.getEmail()),
                () -> assertEquals("123456", user.getSenha())
        );
    }

    @Test
    @DisplayName("Deve adicionar uma avaliação à lista do usuário")
    void user_cenario4() {
        User user = new User("Teste", "teste@email.com", "123");

        AnaliseSentimento analiseMock = Mockito.mock(AnaliseSentimento.class);

        user.adicionarAvaliacao(analiseMock);

        assertEquals(1, user.getAvaliacoes().size());
        assertEquals(analiseMock, user.getAvaliacoes().getFirst());
    }

    @Test
    @DisplayName("Deve impedir adição de avaliação nula lançando exceção")
    void user_cenario5() {
        User user = new User("Teste", "teste@email.com", "123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> user.adicionarAvaliacao(null));

        assertEquals("A avaliação não pode ser nula", exception.getMessage());
        assertTrue(user.getAvaliacoes().isEmpty(), "A lista deve permanecer vazia após a tentativa falha");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Deve falhar validação quando o nome ou senha estiver em branco")
    void user_cenario6(String valorInvalido) {
        User userInvalido = new User(valorInvalido, "teste@email.com", valorInvalido);

        Set<ConstraintViolation<User>> violations = validator.validate(userInvalido);
        Set<String> mensagens = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        assertAll("Validação de Constraints",
                () -> assertFalse(violations.isEmpty()),
                () -> assertTrue(mensagens.contains("O nome é obrigatório")),
                () -> assertTrue(mensagens.contains("A senha é obrigatória"))
        );
    }

    @Test
    @DisplayName("Deve validar campos nome ou senha (Bean Validation)")
    void user_cenario7() {
        User userInvalido = new User(null, "teste@email.com", null);

        Set<ConstraintViolation<User>> violations = validator.validate(userInvalido);
        Set<String> mensagens = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        assertAll("Validação de Constraints",
                () -> assertFalse(violations.isEmpty()),
                () -> assertTrue(mensagens.contains("O nome é obrigatório")),
                () -> assertTrue(mensagens.contains("A senha é obrigatória"))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "email-sem-arroba", "teste@", "teste.com", "@dominio.com", "usuario@.com"})
    @DisplayName("Deve falhar validação quando o e-mail for inválido (vazio ou formato incorreto)")
    void user_cenario8(String emailInvalido) {
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
}
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
class UsuarioTest {

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
        Usuario usuario = new Usuario("Carlos", "carlos@dominio.com", "segredo123");

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

        assertTrue(violations.isEmpty(), "Não deveria haver erros de validação");
    }

    @Test
    @DisplayName("Deve instanciar usuário corretamente usando o construtor com argumentos")
    void user_cenario2() {
        Usuario usuario = new Usuario("João Silva", "joao@email.com", "senha123");

        assertAll("Estado do Usuario",
                () -> assertNull(usuario.getId(), "ID deve ser nulo antes da persistência"),
                () -> assertEquals("João Silva", usuario.getNome()),
                () -> assertEquals("joao@email.com", usuario.getEmail()),
                () -> assertEquals("senha123", usuario.getSenha()),
                () -> assertNotNull(usuario.getAvaliacoes()),
                () -> assertTrue(usuario.getAvaliacoes().isEmpty())
        );
    }

    @Test
    @DisplayName("Deve instanciar usuário corretamente usando construtor vazio e setters")
    void user_cenario3() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        usuario.setSenha("123456");

        assertAll("Validação de Setters",
                () -> assertEquals("Maria", usuario.getNome()),
                () -> assertEquals("maria@email.com", usuario.getEmail()),
                () -> assertEquals("123456", usuario.getSenha())
        );
    }

    @Test
    @DisplayName("Deve adicionar uma avaliação à lista do usuário")
    void user_cenario4() {
        Usuario usuario = new Usuario("Teste", "teste@email.com", "123");

        AnaliseSentimento analiseMock = Mockito.mock(AnaliseSentimento.class);

        usuario.adicionarAvaliacao(analiseMock);

        assertEquals(1, usuario.getAvaliacoes().size());
        assertEquals(analiseMock, usuario.getAvaliacoes().getFirst());
    }

    @Test
    @DisplayName("Deve impedir adição de avaliação nula lançando exceção")
    void user_cenario5() {
        Usuario usuario = new Usuario("Teste", "teste@email.com", "123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> usuario.adicionarAvaliacao(null));

        assertEquals("A avaliação não pode ser nula", exception.getMessage());
        assertTrue(usuario.getAvaliacoes().isEmpty(), "A lista deve permanecer vazia após a tentativa falha");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Deve falhar validação quando o nome ou senha estiver em branco")
    void user_cenario6(String valorInvalido) {
        Usuario usuarioInvalido = new Usuario(valorInvalido, "teste@email.com", valorInvalido);

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuarioInvalido);
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
        Usuario usuarioInvalido = new Usuario(null, "teste@email.com", null);

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuarioInvalido);
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
        Usuario usuario = new Usuario("Nome Válido", emailInvalido, "senha123");

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

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
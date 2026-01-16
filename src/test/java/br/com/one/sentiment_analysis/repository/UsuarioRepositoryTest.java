package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.user.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;


    @Test
    @DisplayName("Deve retornar o usuário correto quando um e-mails ou IDs existentes são fornecidos")
    void userRepository_cenario1() {
        Usuario usuario = new Usuario("Pedro", "pedro@gmail.com", "123456");

        Usuario usuariosalvo = repository.save(usuario);

        Optional<Usuario> buscaPorEmail = repository.findByEmail("pedro@gmail.com");
        Optional<Usuario> buscaPorID = repository.findById(usuariosalvo.getId());

        assertAll("Persistência de Usuário",
                () -> assertNotNull(usuariosalvo.getId()),
                () -> assertTrue(buscaPorEmail.isPresent()),
                () -> assertTrue(buscaPorID.isPresent()),
                () -> assertEquals(usuariosalvo.getEmail(), buscaPorEmail.get().getEmail()),
                () -> assertEquals(usuariosalvo.getNome(), buscaPorID.get().getNome())
        );
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio quando um e-mail ou Id inexistente é fornecido")
    void userRepository_cenario2() {
        Optional<Usuario> user = repository.findByEmail("inexistente@gmail.com");
        assertTrue(user.isEmpty(), "Optional deve estar vazio para email inexistente");
    }

    @Test
    @DisplayName("Deve retornar uma Exception de duplicidade de email")
    void userRepository_cenario3() {
        Usuario u1 = new Usuario("Usuario 1", "duplicate@test.com", "123");
        Usuario u2 = new Usuario("Usuario 2", "duplicate@test.com", "456");

        repository.saveAndFlush(u1);

        assertThrows(
                DataIntegrityViolationException.class, () -> repository.saveAndFlush(u2)
        );
    }
}

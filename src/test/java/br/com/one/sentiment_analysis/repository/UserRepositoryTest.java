package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.user.User;
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
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;


    @Test
    @DisplayName("Deve retornar o usuário correto quando um e-mails ou IDs existentes são fornecidos")
    void userRepository_cenario1() {
        User user = new User("Pedro", "pedro@gmail.com", "123456");

        User usuariosalvo = repository.save(user);

        Optional<User> buscaPorEmail = repository.findByEmail("pedro@gmail.com");
        Optional<User> buscaPorID = repository.findById(usuariosalvo.getId());

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
        Optional<User> user = repository.findByEmail("inexistente@gmail.com");
        assertTrue(user.isEmpty(), "Optional deve estar vazio para email inexistente");
    }

    @Test
    @DisplayName("Deve retornar uma Exception de duplicidade de email")
    void userRepository_cenario3() {
        User u1 = new User("User 1", "duplicate@test.com", "123");
        User u2 = new User("User 2", "duplicate@test.com", "456");

        repository.saveAndFlush(u1);

        assertThrows(
                DataIntegrityViolationException.class, () -> repository.saveAndFlush(u2)
        );
    }
}

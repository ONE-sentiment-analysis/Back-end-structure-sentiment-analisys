package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;


    @Test
    @DisplayName("Deve retornar o usuário correto quando um e-mail ou Id existente é fornecido")
    void userRepository_cenario1() {
        User user = new User("pedro", "pedro@gmail.com", "123");

        repository.save(user);
        Optional<User> userOptional = repository.findByEmail(user.getEmail());
        Optional<User> userOptional2 = repository.findById(user.getId());

        assertFalse(userOptional.isEmpty());
        assertFalse(userOptional2.isEmpty());
        assertEquals(user.getId(), userOptional.get().getId());
        assertEquals(user.getEmail(), userOptional.get().getEmail());
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio quando um e-mail ou Id inexistente é fornecido")
    void userRepository_cenario2() {
        Optional<User> userOptional = repository.findByEmail("");
        Optional<User> userOptional2 = repository.findById(0L);

        assertTrue(userOptional.isEmpty());
        assertTrue(userOptional2.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar uma Exception de duplicidade de email")
    void userRepository_cenario3() {
        var users = List.of(
                new User("pedro", "pedro@gmail.com", "123"),
                new User("pedro", "pedro@gmail.com", "123")
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAll(users);
            repository.flush();
        });
    }
}

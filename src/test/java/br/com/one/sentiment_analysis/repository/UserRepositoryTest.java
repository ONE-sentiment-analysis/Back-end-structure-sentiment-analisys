package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;


    @Test
    void shouldCreateUser() {
        User user = new User("pedro", "pedro@gmail.com", "123");

        User savedUser = repository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("pedro", savedUser.getNome());
    }
}

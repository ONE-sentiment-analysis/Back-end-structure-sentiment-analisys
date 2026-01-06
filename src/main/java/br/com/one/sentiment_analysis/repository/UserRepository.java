package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.user.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(@NonNull Long id);

//    TODO: necessário campo email
//    Optional<T> findByEmail(String nome);
}

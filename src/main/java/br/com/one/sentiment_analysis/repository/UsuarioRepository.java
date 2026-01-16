package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.user.Usuario;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @NonNull Optional<Usuario> findById(@NonNull Long id);

    Optional<Usuario> findByEmail(String email);
}

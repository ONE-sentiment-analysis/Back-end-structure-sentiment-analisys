package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.pessoa.Pessoa;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    Optional<Pessoa> findById(@NonNull Long id);
}

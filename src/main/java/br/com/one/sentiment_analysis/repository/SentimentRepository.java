package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.avaliacao.AnaliseSentimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentimentRepository extends JpaRepository<AnaliseSentimento, Long> {
    Page<AnaliseSentimento> findAllByUserIdOrderByAsc(Long id, Pageable paginacao);
}

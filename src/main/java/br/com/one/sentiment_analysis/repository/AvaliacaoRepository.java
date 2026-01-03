package br.com.one.sentiment_analysis.repository;

import br.com.one.sentiment_analysis.model.AnaliseSentimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRepository extends JpaRepository<AnaliseSentimento, Long> {
    @Query("SELECT a FROM AnaliseSentimento a WHERE a.idReferencia.valor LIKE CONCAT('prod_', :idProduto, '_review_%')")
    Page<AnaliseSentimento> buscarPorIdProduto(@Param("idProduto") Long idProduto, Pageable pageable);
}

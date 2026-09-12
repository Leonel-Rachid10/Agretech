package com.agritech.dondo.repository;

import com.agritech.dondo.model.EstadoLote;
import com.agritech.dondo.model.LoteProducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;


public interface LoteProducaoRepository extends JpaRepository<LoteProducao, Long> {

    List<LoteProducao> findByEstado(EstadoLote estado);

    List<LoteProducao> findByProdutorId(Long produtorId);

    List<LoteProducao> findByProdutorAssociacaoId(Long associacaoId);

    @Query("SELECT l FROM LoteProducao l WHERE " +
           "(:culturaId IS NULL OR l.cultura.id = :culturaId) AND " +
           "(:localidade IS NULL OR LOWER(l.produtor.associacao.localidade) LIKE LOWER(CONCAT('%', :localidade, '%'))) AND " +
           "(:estado IS NULL OR l.estado = :estado) AND " +
           "(:quantidadeMinima IS NULL OR l.quantidadeEstimada >= :quantidadeMinima) " +
           "ORDER BY l.dataColheitaPrevista ASC")
    List<LoteProducao> pesquisarCatalogo(
            @Param("culturaId") Long culturaId,
            @Param("localidade") String localidade,
            @Param("estado") EstadoLote estado,
            @Param("quantidadeMinima") BigDecimal quantidadeMinima
    );
}

package org.fiap.gs_final.repository;

import org.fiap.gs_final.model.Orientacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrientacaoRepository extends JpaRepository<Orientacao, Long> {

    Page<Orientacao> findByOcorrenciaId(Long ocorrenciaId, Pageable pageable);

    /** Antes de excluir um local seguro, tira o vínculo dele das orientações já geradas. */
    @Modifying
    @Query(value = "DELETE FROM tb_orientacao_local WHERE local_id = :localId", nativeQuery = true)
    void desvincularLocal(@Param("localId") Long localId);
}

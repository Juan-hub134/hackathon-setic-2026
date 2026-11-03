package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.Historico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico,Long> {
    List<Historico> findAllByTabelaAndIdRegistroOrderByAlteradoEmDescIdDesc(
            String tabela,
            long idRegistro
    );

    @Query("""
            SELECT h
            FROM Historico h
            WHERE (:tabela IS NULL OR h.tabela = :tabela)
              AND (:idRegistro IS NULL OR h.idRegistro = :idRegistro)
            """)
    Page<Historico> buscarComFiltros(
            @Param("tabela") String tabela,
            @Param("idRegistro") Long idRegistro,
            Pageable pageable
    );

    Optional<Historico> findTopByTabelaAndIdRegistroAndOperacaoInOrderByAlteradoEmDesc(
            String tabela,
            Long idRegistro,
            List<String> operacoes
    );
    Optional<Historico> findFirstByTabelaAndIdRegistroAndOperacaoOrderByIdDesc(
            String tabela,
            long idRegistro,
            String operacao
    );
}

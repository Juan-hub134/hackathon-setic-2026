package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.Historico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico,Long> {
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

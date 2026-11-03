package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.SolicitacaoCsgr;
import br.com.hackathon.setic.model.StatusSolicitacaoCsgr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitacaoCsgrRepository extends JpaRepository<SolicitacaoCsgr, Long> {
    boolean existsByPlanoRespostaIdRespostaAndStatus(Long idResposta, StatusSolicitacaoCsgr status);

    List<SolicitacaoCsgr> findByStatusOrderBySolicitadoEmAsc(StatusSolicitacaoCsgr status);

    List<SolicitacaoCsgr> findByUsuarioSolicitanteIdUsuarioOrderBySolicitadoEmDesc(long idUsuario);

    Optional<SolicitacaoCsgr> findFirstByPlanoRespostaIdRespostaAndStatusOrderBySolicitadoEmAsc(
            Long idResposta,
            StatusSolicitacaoCsgr status
    );
}

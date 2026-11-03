package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.PlanoResposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanoRespostaRepository extends JpaRepository<PlanoResposta, Long> {

    @Query("SELECT pr FROM PlanoResposta pr WHERE pr.contexto.idContexto = :idContexto")
    Optional<PlanoResposta> findByContextoId(@Param("idContexto") Long idContexto);
}

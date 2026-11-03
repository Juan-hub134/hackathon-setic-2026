package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.ParecerPrr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParecerPrrRepository extends JpaRepository<ParecerPrr, Long> {

    @Query("SELECT p FROM ParecerPrr p WHERE p.planoResposta.idResposta = :idResposta ORDER BY p.avaliadoEm DESC")
    List<ParecerPrr> findByPlanoRespostaIdOrderByAvaliadoEmDesc(@Param("idResposta") Long idResposta);
}

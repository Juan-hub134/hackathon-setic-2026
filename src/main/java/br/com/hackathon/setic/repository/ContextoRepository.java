package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.Contexto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContextoRepository extends JpaRepository<Contexto, Long> {

    @Query("SELECT c FROM Contexto c WHERE c.risco.idRisco = :idRisco")
    Optional<Contexto> findByRiscoId(@Param("idRisco") Long idRisco);
}

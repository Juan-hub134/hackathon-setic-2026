package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.ContextoEtapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContextoEtapaRepository extends JpaRepository<ContextoEtapa, Long> {

    @Query("SELECT ce FROM ContextoEtapa ce WHERE ce.contexto.idContexto = :idContexto")
    List<ContextoEtapa> findByContextoId(@Param("idContexto") Long idContexto);
}

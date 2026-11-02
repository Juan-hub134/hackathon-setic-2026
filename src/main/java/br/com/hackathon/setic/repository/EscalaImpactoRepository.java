package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.EscalaImpacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscalaImpactoRepository extends JpaRepository<EscalaImpacto, Integer> {
}

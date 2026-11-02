package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.EscalaProbabilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscalaProbabilidadeRepository extends JpaRepository<EscalaProbabilidade,Long> {
}

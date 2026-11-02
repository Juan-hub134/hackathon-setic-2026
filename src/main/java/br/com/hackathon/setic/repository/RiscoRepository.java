package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.Risco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiscoRepository extends JpaRepository<Risco, Long> {
}

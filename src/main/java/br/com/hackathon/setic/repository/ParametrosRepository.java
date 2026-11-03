package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametrosRepository extends JpaRepository<Parametros, Long> {
}

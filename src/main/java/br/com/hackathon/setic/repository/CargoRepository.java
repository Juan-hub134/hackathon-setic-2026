package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargoRepository extends JpaRepository<Cargo, Long> {
}

package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.Risco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiscoRepository extends JpaRepository<Risco, Long> {
    @Query(
            value = """
            SELECT r.*
            FROM risco r
            JOIN usuario u ON r.id_usuario = u.id_usuario
            WHERE u.id_setor = :idSetor
""", nativeQuery = true)
    List<Risco> buscarRiscoPorSetor(long idSetor);
}

package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    @Query(" SELECT u FROM Usuario u WHERE u.setor.idSetor = :idSetor")
    List<Usuario> fltrarPorSetorDoGestor(long idSetor);
}
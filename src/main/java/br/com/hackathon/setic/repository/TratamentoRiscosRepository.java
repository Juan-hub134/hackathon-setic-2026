package br.com.hackathon.setic.repository;

import br.com.hackathon.setic.model.TratamentoRiscos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TratamentoRiscosRepository extends JpaRepository<TratamentoRiscos, Long> {
}

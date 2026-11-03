package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.TratamentoResponseDto;
import br.com.hackathon.setic.repository.TratamentoRiscosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TratamentoRiscosService {
    private final TratamentoRiscosRepository tratamentoRiscosRepository;

    public TratamentoRiscosService(TratamentoRiscosRepository tratamentoRiscosRepository) {
        this.tratamentoRiscosRepository = tratamentoRiscosRepository;
    }

    public List<TratamentoResponseDto> listarTodos() {
        return tratamentoRiscosRepository.findAll().stream()
                .map(t -> new TratamentoResponseDto(
                        t.getIdTratamento(),
                        t.getRotulo(),
                        t.getDescricao()
                ))
                .toList();
    }
}

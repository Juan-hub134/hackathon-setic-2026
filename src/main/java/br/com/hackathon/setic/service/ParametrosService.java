package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.ParametrosResponseDto;
import br.com.hackathon.setic.repository.ParametrosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParametrosService {
    private final ParametrosRepository parametrosRepository;

    public ParametrosService(ParametrosRepository parametrosRepository) {
        this.parametrosRepository = parametrosRepository;
    }

    public List<ParametrosResponseDto> listarTodos() {
        return parametrosRepository.findAll().stream()
                .map(p -> new ParametrosResponseDto(
                        p.getIdParametro(),
                        p.getRotulo(),
                        p.getDescricao()
                ))
                .toList();
    }
}

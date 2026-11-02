package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.EscalaImpactoResponseDto;
import br.com.hackathon.setic.model.EscalaImpacto;
import br.com.hackathon.setic.repository.EscalaImpactoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EscalaImpactoService {
    private final EscalaImpactoRepository escalaImpactoRepository;
    public EscalaImpactoService(EscalaImpactoRepository repository) {
        this.escalaImpactoRepository = repository;
    }
    public List<EscalaImpactoResponseDto> escalaProbabilidades(){
        List<EscalaImpacto> impactos =escalaImpactoRepository.findAll();
        List<EscalaImpactoResponseDto> responseDto = impactos.stream()
                .map(m -> new EscalaImpactoResponseDto(m.getNota(),m.getRotulo(),m.getDescricao()))
                .toList();
        return responseDto;
    }
}

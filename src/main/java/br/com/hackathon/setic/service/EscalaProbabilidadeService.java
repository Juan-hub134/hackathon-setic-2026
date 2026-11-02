package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.CategoriaResponseDto;
import br.com.hackathon.setic.dto.response.EscalaProbabilidadeResponseDto;
import br.com.hackathon.setic.model.EscalaProbabilidade;
import br.com.hackathon.setic.repository.CargoRepository;
import br.com.hackathon.setic.repository.EscalaProbabilidadeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EscalaProbabilidadeService {
    private final EscalaProbabilidadeRepository escalaProbabilidadeRepository;
    public EscalaProbabilidadeService(EscalaProbabilidadeRepository escalaRepository) {
        this.escalaProbabilidadeRepository = escalaRepository;
    }
    public List<EscalaProbabilidadeResponseDto> getEscalasProbabilidades(){
        List<EscalaProbabilidade> escalaProbabilidades = escalaProbabilidadeRepository.findAll();
        List<EscalaProbabilidadeResponseDto> responseDto = escalaProbabilidades.stream()
                .map(m -> new EscalaProbabilidadeResponseDto(m.getNota(), m.getRotulo(), m.getDescricao()) )
                .toList();
        return responseDto;
    }
}

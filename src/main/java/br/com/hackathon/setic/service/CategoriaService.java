package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.CategoriaResponseDto;
import br.com.hackathon.setic.dto.response.EscalaProbabilidadeResponseDto;
import br.com.hackathon.setic.model.Categoria;
import br.com.hackathon.setic.model.EscalaProbabilidade;
import br.com.hackathon.setic.repository.CategoriaRepository;
import br.com.hackathon.setic.repository.EscalaProbabilidadeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final EscalaProbabilidadeRepository escalaProbabilidadeRepository;
    public CategoriaService(CategoriaRepository categoriaRepository, EscalaProbabilidadeRepository escalaProbabilidadeRepository) {
        this.categoriaRepository = categoriaRepository;
        this.escalaProbabilidadeRepository = escalaProbabilidadeRepository;
    }
    public List<CategoriaResponseDto> categoria(){
        List<Categoria> categorias = categoriaRepository.findAll();
        List<CategoriaResponseDto> categoriasResponseDto = categorias.stream()
                .map(m -> new CategoriaResponseDto(m.getIdCategoria(), m.getNome(), m.getDescricao()))
                .toList();
        return categoriasResponseDto;
    }
}

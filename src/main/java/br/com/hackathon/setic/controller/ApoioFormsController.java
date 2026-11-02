package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.response.CategoriaResponseDto;
import br.com.hackathon.setic.dto.response.EscalaImpactoResponseDto;
import br.com.hackathon.setic.dto.response.EscalaProbabilidadeResponseDto;
import br.com.hackathon.setic.service.CategoriaService;
import br.com.hackathon.setic.service.EscalaImpactoService;
import br.com.hackathon.setic.service.EscalaProbabilidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
//apoio ao formulário
public class ApoioFormsController {
    private final CategoriaService categoriaService;
    private final EscalaProbabilidadeService escalaProbabilidadeService;
    private final EscalaImpactoService escalaImpactoService;
    public ApoioFormsController(CategoriaService categoriaService, EscalaProbabilidadeService escalaProbabilidadeService, EscalaImpactoService escalaImpactoService) {
        this.categoriaService = categoriaService;
        this.escalaProbabilidadeService = escalaProbabilidadeService;
        this.escalaImpactoService = escalaImpactoService;
    }
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResponseDto>> getCategorias(){
        return ResponseEntity.ok(categoriaService.categoria());
    }
    @GetMapping("/escalas/probabilidade")
    public ResponseEntity<List<EscalaProbabilidadeResponseDto>> getEscalasProbabilidades(){
        return ResponseEntity.ok(escalaProbabilidadeService.getEscalasProbabilidades());
    }
    @GetMapping("/escalas/impacto")
    public ResponseEntity<List<EscalaImpactoResponseDto>> getEscalasImpactos(){
        return ResponseEntity.ok(escalaImpactoService.escalaProbabilidades());
    }
}

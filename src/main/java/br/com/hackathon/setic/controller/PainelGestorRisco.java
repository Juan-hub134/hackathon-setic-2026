package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.response.ListaSetorResponseDto;
import br.com.hackathon.setic.service.PainelGestorRiscoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class PainelGestorRisco {
    private final PainelGestorRiscoService painelGestorRiscoService;
    public PainelGestorRisco(PainelGestorRiscoService painelGestorRiscoService) {
        this.painelGestorRiscoService = painelGestorRiscoService;
    }
    @GetMapping("/gestor/setor/lista")
    public List<ListaSetorResponseDto>lista(@AuthenticationPrincipal Jwt jwt){
            String sub = jwt.getSubject();
            return painelGestorRiscoService.listaDeFuncionariosDoSetorDoGestor(sub);
    }
   //servidor ou ascf
   @PostMapping("/gestor/indicar/{id}")
  public ResponseEntity<?> indicarSetor(@AuthenticationPrincipal Jwt jwt, @PathVariable long id){
        String sub = jwt.getSubject();
        painelGestorRiscoService.indicar(id, sub);
        return  ResponseEntity.ok("cargo alterado com sucesso!");
   }
}

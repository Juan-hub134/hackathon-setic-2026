package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.request.RiscoRequestDto;
import br.com.hackathon.setic.dto.response.RiscoResponseDto;
import br.com.hackathon.setic.service.RiscoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RiscoController {
    private RiscoService riscoService;
    public RiscoController(RiscoService riscoService){
        this.riscoService = riscoService;
    }
    @PostMapping("/registrar/risco")
    public ResponseEntity<String> registrarRisco(@RequestBody RiscoRequestDto dto, @AuthenticationPrincipal Jwt jwt){
        String sub = jwt.getSubject();
        riscoService.registrarRisco(dto,sub);
        return ResponseEntity.ok("Risco registrado com sucesso");
    }
    @GetMapping("/riscos")//filtrada pelo setor
    public ResponseEntity<List<RiscoResponseDto>> listarRiscos(@AuthenticationPrincipal Jwt jwt){
        String sub = jwt.getSubject();
        return ResponseEntity.ok(riscoService.listarRiscosPeloSetorGestor(sub));
    }

    @GetMapping("riscos/setores")//risco de todos os setores. obs: rota disponível somente para CGGE e CSGR
    public ResponseEntity<List<RiscoResponseDto>> listarRiscosSetores(){
        return ResponseEntity.ok(riscoService.listarRiscosDeTodosOsSetores());
    }
    @GetMapping("risco/filtrar/{id}")  //Qualquer pessoa com CGGE e CSGR
    public ResponseEntity<RiscoResponseDto> filtrarRisco(@PathVariable long id){
        return ResponseEntity.ok(riscoService.filtrarTodosOsRiscos(id));
    }
}

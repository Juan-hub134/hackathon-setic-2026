package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.request.ContextoRequestDto;
import br.com.hackathon.setic.dto.response.ContextoResponseDto;
import br.com.hackathon.setic.service.ContextoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ContextoController {
    private final ContextoService contextoService;

    public ContextoController(ContextoService contextoService) {
        this.contextoService = contextoService;
    }

    @PostMapping("/contexto")
    public ResponseEntity<ContextoResponseDto> criarContexto(@RequestBody ContextoRequestDto dto, @AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getSubject();
        return ResponseEntity.ok(contextoService.criarContexto(dto, sub));
    }

    @GetMapping("/contexto/risco/{idRisco}")
    public ResponseEntity<ContextoResponseDto> buscarContextoPorRisco(@PathVariable Long idRisco, @AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getSubject();
        return ResponseEntity.ok(contextoService.buscarContextoPorRisco(idRisco, sub));
    }

    @PutMapping("/contexto/etapa/{idEtapa}/concluir")
    public ResponseEntity<String> concluirEtapa(@PathVariable Long idEtapa, @AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getSubject();
        contextoService.concluirEtapa(idEtapa, sub);
        return ResponseEntity.ok("Etapa concluída com sucesso");
    }
}

package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.request.PlanoRespostaRequestDto;
import br.com.hackathon.setic.dto.response.PlanoRespostaResponseDto;
import br.com.hackathon.setic.service.PlanoRespostaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PlanoRespostaController {
    private final PlanoRespostaService planoRespostaService;

    public PlanoRespostaController(PlanoRespostaService planoRespostaService) {
        this.planoRespostaService = planoRespostaService;
    }

    @PostMapping("/prr")
    public ResponseEntity<PlanoRespostaResponseDto> criarPrr(@RequestBody PlanoRespostaRequestDto dto, @AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getSubject();
        return ResponseEntity.ok(planoRespostaService.criarPlanoResposta(dto, sub));
    }

    @GetMapping("/prr/contexto/{idContexto}")
    public ResponseEntity<PlanoRespostaResponseDto> buscarPrrPorContexto(@PathVariable Long idContexto, @AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getSubject();
        return ResponseEntity.ok(planoRespostaService.buscarPrrPorContexto(idContexto, sub));
    }

    @PutMapping("/prr/{id}")
    public ResponseEntity<PlanoRespostaResponseDto> atualizarPrr(@PathVariable Long id, @RequestBody PlanoRespostaRequestDto dto, @AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getSubject();
        return ResponseEntity.ok(planoRespostaService.atualizarPrr(id, dto, sub));
    }
}

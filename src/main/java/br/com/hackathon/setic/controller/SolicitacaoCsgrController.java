package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.request.SolicitacaoCsgrRequestDto;
import br.com.hackathon.setic.dto.response.SolicitacaoCsgrResponseDto;
import br.com.hackathon.setic.service.SolicitacaoCsgrService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/csgr/solicitacoes")
public class SolicitacaoCsgrController {
    private final SolicitacaoCsgrService solicitacaoService;

    public SolicitacaoCsgrController(SolicitacaoCsgrService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    public ResponseEntity<SolicitacaoCsgrResponseDto> solicitar(
            @Valid @RequestBody SolicitacaoCsgrRequestDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(solicitacaoService.solicitar(dto.idResposta(), dto.motivo(), jwt.getSubject()));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<SolicitacaoCsgrResponseDto>> listarPendentes(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(solicitacaoService.listarPendentes(jwt.getSubject()));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<SolicitacaoCsgrResponseDto>> listarMinhas(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(solicitacaoService.listarMinhas(jwt.getSubject()));
    }
}

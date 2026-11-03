package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.response.HistoricoPaginaResponseDto;
import br.com.hackathon.setic.dto.response.HistoricoResponseDto;
import br.com.hackathon.setic.service.HistoricoConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historico")
public class HistoricoController {
    private final HistoricoConsultaService historicoConsultaService;

    public HistoricoController(HistoricoConsultaService historicoConsultaService) {
        this.historicoConsultaService = historicoConsultaService;
    }

    @GetMapping("/{tabela}/{id}")
    public ResponseEntity<List<HistoricoResponseDto>> listarPorRegistro(
            @PathVariable String tabela,
            @PathVariable long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                historicoConsultaService.listarPorRegistro(tabela, id, jwt.getSubject())
        );
    }

    @GetMapping
    public ResponseEntity<HistoricoPaginaResponseDto> listarTodos(
            @RequestParam(required = false) String tabela,
            @RequestParam(required = false) Long idRegistro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                historicoConsultaService.listarTodos(
                        tabela,
                        idRegistro,
                        page,
                        size,
                        jwt.getSubject()
                )
        );
    }
}

package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.request.ParecerRequestDto;
import br.com.hackathon.setic.dto.response.ParecerResponseDto;
import br.com.hackathon.setic.service.ParecerPrrService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ParecerPrrController {
    private final ParecerPrrService parecerPrrService;

    public ParecerPrrController(ParecerPrrService parecerPrrService) {
        this.parecerPrrService = parecerPrrService;
    }

    @PostMapping("/parecer")
    public ResponseEntity<ParecerResponseDto> avaliarPrr(@Valid @RequestBody ParecerRequestDto dto, @AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getSubject();
        return ResponseEntity.ok(parecerPrrService.avaliarPrr(dto, sub));
    }

    @GetMapping("/parecer/prr/{idResposta}")
    public ResponseEntity<List<ParecerResponseDto>> buscarHistoricoParecer(@PathVariable Long idResposta) {
        return ResponseEntity.ok(parecerPrrService.buscarHistoricoParecesPorPrr(idResposta));
    }
}

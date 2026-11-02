package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.request.RiscoRequestDto;
import br.com.hackathon.setic.model.Risco;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.service.RiscoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
}

package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.response.ParametrosResponseDto;
import br.com.hackathon.setic.service.ParametrosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ParametrosController {
    private final ParametrosService parametrosService;

    public ParametrosController(ParametrosService parametrosService) {
        this.parametrosService = parametrosService;
    }

    @GetMapping("/parametros")
    public ResponseEntity<List<ParametrosResponseDto>> listarParametros() {
        return ResponseEntity.ok(parametrosService.listarTodos());
    }
}

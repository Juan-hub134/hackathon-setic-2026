package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.response.TratamentoResponseDto;
import br.com.hackathon.setic.service.TratamentoRiscosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TratamentoRiscosController {
    private final TratamentoRiscosService tratamentoRiscosService;

    public TratamentoRiscosController(TratamentoRiscosService tratamentoRiscosService) {
        this.tratamentoRiscosService = tratamentoRiscosService;
    }

    @GetMapping("/tratamentos")
    public ResponseEntity<List<TratamentoResponseDto>> listarTratamentos() {
        return ResponseEntity.ok(tratamentoRiscosService.listarTodos());
    }
}

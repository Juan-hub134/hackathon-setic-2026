package br.com.hackathon.setic.dto.request;

import java.time.LocalDate;
import java.util.List;

public record ContextoRequestDto(
        Long idRisco,
        String escopo,
        List<EtapaDto> etapas
) {
    public record EtapaDto(
            String etapa,
            LocalDate prazo
    ) {}
}

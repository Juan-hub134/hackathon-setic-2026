package br.com.hackathon.setic.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ContextoResponseDto(
        Long idContexto,
        Long idRisco,
        String escopo,
        LocalDateTime criadoEm,
        List<EtapaResponseDto> etapas
) {
    public record EtapaResponseDto(
            Long idContextoEtapa,
            String etapa,
            LocalDate prazo,
            LocalDate concluidoEm,
            Boolean status
    ) {}
}

package br.com.hackathon.setic.dto.response;

import java.util.List;

public record HistoricoPaginaResponseDto(
        List<HistoricoResponseDto> itens,
        int page,
        int size,
        long totalElementos,
        int totalPaginas
) {
}

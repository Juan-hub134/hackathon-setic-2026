package br.com.hackathon.setic.dto.response;

import java.time.LocalDateTime;

public record HistoricoResponseDto(
        long id,
        String tabela,
        long idRegistro,
        String operacao,
        String descricao,
        QuemDto quem,
        LocalDateTime quando
) {
    public record QuemDto(Long id, String nome) {
    }
}

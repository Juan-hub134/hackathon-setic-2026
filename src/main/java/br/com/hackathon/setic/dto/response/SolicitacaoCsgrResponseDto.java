package br.com.hackathon.setic.dto.response;

import br.com.hackathon.setic.model.StatusPrr;
import br.com.hackathon.setic.model.StatusSolicitacaoCsgr;

import java.time.LocalDateTime;

public record SolicitacaoCsgrResponseDto(
        Long idSolicitacao,
        Long idResposta,
        Long idRisco,
        String descricaoRisco,
        String setor,
        EscalaResponseDto probabilidade,
        EscalaResponseDto impacto,
        long nivel,
        String nomeSolicitante,
        String motivo,
        LocalDateTime solicitadoEm,
        LocalDateTime atendidoEm,
        StatusSolicitacaoCsgr status,
        StatusPrr statusPrr,
        String escopo,
        String tratamento,
        String parametro,
        String nomeResponsaveis,
        String controlesExistentes,
        String acaoProposta,
        String metricas
) {}

package br.com.hackathon.setic.dto.response;

import br.com.hackathon.setic.model.StatusPrr;

public record PlanoRespostaResponseDto(
        Long idResposta,
        Long idContexto,
        String tratamento,
        String parametro,
        String nomeResponsaveis,
        String controlesExistentes,
        String acaoProposta,
        String metricas,
        StatusPrr status
) {}

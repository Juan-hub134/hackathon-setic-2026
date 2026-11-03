package br.com.hackathon.setic.dto.request;

public record PlanoRespostaRequestDto(
        Long idContexto,
        Long idTratamento,
        Long idParametro,
        String nomeResponsaveis,
        String controlesExistentes,
        String acaoProposta,
        String metricas
) {}

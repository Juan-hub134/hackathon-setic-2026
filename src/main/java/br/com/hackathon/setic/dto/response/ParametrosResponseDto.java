package br.com.hackathon.setic.dto.response;

public record ParametrosResponseDto(
        Long idParametro,
        String rotulo,
        String descricao
) {}

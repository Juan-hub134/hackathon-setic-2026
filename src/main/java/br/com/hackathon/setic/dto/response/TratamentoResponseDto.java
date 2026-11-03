package br.com.hackathon.setic.dto.response;

public record TratamentoResponseDto(
        Long idTratamento,
        String rotulo,
        String descricao
) {}

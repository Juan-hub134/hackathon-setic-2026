package br.com.hackathon.setic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitacaoCsgrRequestDto(
        @NotNull Long idResposta,
        @NotBlank @Size(max = 2000) String motivo
) {}

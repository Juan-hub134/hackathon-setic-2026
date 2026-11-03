package br.com.hackathon.setic.dto.request;

import br.com.hackathon.setic.model.StatusPrr;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParecerRequestDto(
        @NotNull Long idResposta,
        @NotNull StatusPrr statusResultado,
        @NotBlank @Size(max = 4000) String parecerTexto
) {}

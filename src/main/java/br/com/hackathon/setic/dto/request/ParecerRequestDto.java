package br.com.hackathon.setic.dto.request;

import br.com.hackathon.setic.model.StatusPrr;

public record ParecerRequestDto(
        Long idResposta,
        StatusPrr statusResultado,
        String parecerTexto
) {}

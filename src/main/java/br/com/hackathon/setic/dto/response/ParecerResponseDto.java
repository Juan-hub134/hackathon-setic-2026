package br.com.hackathon.setic.dto.response;

import br.com.hackathon.setic.model.StatusPrr;
import java.time.LocalDateTime;

public record ParecerResponseDto(
        Long idParecer,
        Long idResposta,
        String nomeAvaliador,
        StatusPrr statusResultado,
        String parecerTexto,
        LocalDateTime avaliadoEm
) {}

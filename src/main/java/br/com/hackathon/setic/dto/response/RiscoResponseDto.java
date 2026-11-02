package br.com.hackathon.setic.dto.response;

import br.com.hackathon.setic.model.EscalaImpacto;
import br.com.hackathon.setic.model.EscalaProbabilidade;

import java.time.LocalDateTime;
import java.util.List;

public record RiscoResponseDto(long idRisco,
                               String setor,
                               String categoria,
                               String descricao,
                               EscalaResponseDto probabilidade,//nota e rotulo
                               EscalaResponseDto impacto,//nota e rotulo
                               long nivel,
                               int horizonteMeses,
                               String criadoPor,
                               LocalDateTime criadoEm) {
}

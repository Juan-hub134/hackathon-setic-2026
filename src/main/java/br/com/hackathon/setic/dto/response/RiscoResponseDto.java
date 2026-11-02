package br.com.hackathon.setic.dto.response;

import br.com.hackathon.setic.model.EscalaImpacto;
import br.com.hackathon.setic.model.EscalaProbabilidade;

import java.time.LocalDateTime;
import java.util.List;

public record RiscoResponseDto(long idRisco,
                               String setor,
                               String categoria,
                               List<EscalaProbabilidade> probabilidade,//nota e rotulo
                               List<EscalaImpacto> impacto,//nota e rotulo
                               long nivel,
                               String faixa,
                               String criadoPor,
                               LocalDateTime criadoEm) {
}

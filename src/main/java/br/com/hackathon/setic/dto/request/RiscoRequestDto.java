package br.com.hackathon.setic.dto.request;

public record RiscoRequestDto(long idCategoria,
                              String causa,
                              String evento,
                              String impactoTxt,
                              String objetivo,
                              long idProbabilidade,
                              long idImpacto,
                              int horizonteMeses) {
}

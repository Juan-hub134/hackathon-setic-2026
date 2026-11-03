package br.com.hackathon.setic.service;

import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.HistoricoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Extensão do HistoricoService com métodos adicionais para Contexto e PRR.
 * Adicione estes métodos ao HistoricoService existente:
 */
@Component
public class HistoricoServiceExtension {
    private final HistoricoRepository historicoRepository;
    private final ObjectMapper objectMapper;

    public HistoricoServiceExtension(HistoricoRepository historicoRepository, ObjectMapper objectMapper) {
        this.historicoRepository = historicoRepository;
        this.objectMapper = objectMapper;
    }

    public void registrarContextoNovo(Usuario usuario, Contexto contexto) {
        Historico historico = new Historico();

        historico.setUsuario(usuario);
        historico.setIdRegistro(contexto.getIdContexto());
        historico.setTabela("contexto");
        historico.setOperacao("CRIAR_CONTEXTO");
        historico.setAlteradoEm(LocalDateTime.now());
        historico.setCamposAlterados("todos");

        historico.setAntes(objectMapper.valueToTree(Map.of()));

        historico.setDepois(
                objectMapper.valueToTree(
                        Map.of(
                                "id_contexto", contexto.getIdContexto(),
                                "id_risco", contexto.getRisco().getIdRisco(),
                                "escopo", contexto.getEscopo()
                        )
                )
        );

        historicoRepository.save(historico);
    }

    public void registrarPrrNovo(Usuario usuario, PlanoResposta prr) {
        Historico historico = new Historico();

        historico.setUsuario(usuario);
        historico.setIdRegistro(prr.getIdResposta());
        historico.setTabela("plano_resposta");
        historico.setOperacao("CRIAR_PRR");
        historico.setAlteradoEm(LocalDateTime.now());
        historico.setCamposAlterados("todos");

        historico.setAntes(objectMapper.valueToTree(Map.of()));

        historico.setDepois(
                objectMapper.valueToTree(
                        Map.of(
                                "id_resposta", prr.getIdResposta(),
                                "id_contexto", prr.getContexto().getIdContexto(),
                                "id_tratamento", prr.getTratamento().getIdTratamento(),
                                "nome_responsaveis", prr.getNomeResponsaveis(),
                                "acao_proposta", prr.getAcaoProposta()
                        )
                )
        );

        historicoRepository.save(historico);
    }
}

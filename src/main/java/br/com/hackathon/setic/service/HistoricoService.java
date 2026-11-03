package br.com.hackathon.setic.service;

import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.HistoricoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class HistoricoService {
    private final HistoricoRepository historicoRepository;
    private final ObjectMapper objectMapper;
    public HistoricoService(
            HistoricoRepository historicoRepository,
            ObjectMapper objectMapper) {

        this.historicoRepository = historicoRepository;
        this.objectMapper = objectMapper;
    }
    public void registrarAlteracaoCargo(
            Usuario gestor,
            Usuario funcionario,
            Long cargoAnterior,
            Long cargoNovo) {
        Historico historico = new Historico();
        historico.setUsuario(gestor);
        historico.setIdRegistro(funcionario.getIdUsuario());
        historico.setTabela("usuario");
        historico.setOperacao("ATRIBUIR_PRR");
        historico.setAlteradoEm(LocalDateTime.now());
        historico.setCamposAlterados("id_cargo");
        historico.setAntes(
                objectMapper.valueToTree(
                        Map.of("id_cargo", cargoAnterior)
                )
        );

        historico.setDepois(
                objectMapper.valueToTree(
                        Map.of("id_cargo", cargoNovo)
                )
        );
        historicoRepository.save(historico);
    }
    public void voltarAoCargoAntigo(Usuario gestor, Usuario funcionario, Long cargoAnterior, Long cargoNovo ){
        Historico historico = new Historico();
        historico.setUsuario(gestor);
        historico.setIdRegistro(funcionario.getIdUsuario());
        historico.setTabela("usuario");
        historico.setOperacao("rollback");
        historico.setAlteradoEm(LocalDateTime.now());
        historico.setCamposAlterados("id_cargo");
        historico.setAntes(
                objectMapper.valueToTree(
                        Map.of("id_cargo", cargoAnterior)
                )
        );
        historico.setDepois(
                objectMapper.valueToTree(
                        Map.of("id_cargo", cargoNovo)
                )
        );
        historicoRepository.save(historico);
    }
    public void registrarRiscoNovo(Usuario usuario, Risco risco) {
        Historico historico = new Historico();

        historico.setUsuario(usuario);
        historico.setIdRegistro(risco.getIdRisco());
        historico.setTabela("risco");
        historico.setOperacao("REGISTRAR_RISCO");
        historico.setAlteradoEm(LocalDateTime.now());
        historico.setCamposAlterados(
                "id_risco,id_categoria,probabilidade,impacto,nivel,horizonte_meses"
        );

        historico.setAntes(null);

        historico.setDepois(
                objectMapper.valueToTree(
                        Map.of(
                                "id_risco", risco.getIdRisco(),
                                "id_categoria", risco.getCategoria().getIdCategoria(),
                                "probabilidade", risco.getEscalaProbabilidade().getNota(),
                                "impacto", risco.getEscalaImpacto().getNota(),
                                "nivel", risco.getNivel(),
                                "horizonte_meses", risco.getHorizonteMeses()
                        )
                )
        );
        historicoRepository.save(historico);
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

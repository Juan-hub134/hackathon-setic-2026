package br.com.hackathon.setic.service;

import br.com.hackathon.setic.model.Historico;
import br.com.hackathon.setic.model.Risco;
import br.com.hackathon.setic.model.Usuario;
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
        historico.setCamposAlterados("todos");

        historico.setAntes(
                objectMapper.valueToTree(Map.of())
        );

        historico.setDepois(
                objectMapper.valueToTree(
                        Map.of(
                                "id_risco", risco.getIdRisco(),
                                "id_categoria", risco.getCategoria().getIdCategoria(),
                                "causa", risco.getCausa(),
                                "evento", risco.getEvento(),
                                "impacto_txt", risco.getImpactoTxt(),
                                "objetivo", risco.getObjetivo(),
                                "descricao", risco.getDescricao(),
                                "id_probabilidade", risco.getEscalaProbabilidade().getNota(),
                                "id_impacto", risco.getEscalaImpacto().getNota(),
                                "horizonte_meses", risco.getHorizonteMeses()
                        )
                )
        );
        historicoRepository.save(historico);
    }
}

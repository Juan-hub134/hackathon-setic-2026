package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.ListaSetorResponseDto;
import br.com.hackathon.setic.exception.QuantidadeException;
import br.com.hackathon.setic.model.Cargo;
import br.com.hackathon.setic.model.Historico;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.CargoRepository;
import br.com.hackathon.setic.repository.HistoricoRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PainelGestorRiscoService {
    private final UsuarioRepository usuarioRepository;
    private final HistoricoService historicoService;
    private final CargoRepository cargoRepository;
    private final HistoricoRepository historicoRepository;
    private static final Set<String> CARGOS_VISIVEIS = Set.of("Servidor", "ASCF", "RESPONSAVEL_PRR")
    private static final long ID_CARGO_RESPONSAVEL_PRR = 4L;
    public PainelGestorRiscoService(UsuarioRepository usuarioRepository, HistoricoService historicoService, CargoRepository cargoRepository,
                                    HistoricoRepository historicoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.historicoService = historicoService;
        this.cargoRepository = cargoRepository;
        this.historicoRepository = historicoRepository;
    }
    public List<ListaSetorResponseDto> listaDeFuncionariosDoSetorDoGestor(String sub){
        long id = Long.parseLong(sub);

        Usuario usuario = usuarioRepository.findById(id).get();
        long idSetor = usuario.getSetor().getIdSetor();
        List<Usuario> listaDeFuncionariosDoSetorDoGestor = usuarioRepository.fltrarPorSetorDoGestor(idSetor);
        List<ListaSetorResponseDto> listaFiltrada = listaDeFuncionariosDoSetorDoGestor.stream()
                .filter(lista -> lista.getIdUsuario() != id)
                .filter(lista -> CARGOS_VISIVEIS.contains(lista.getCargo().getCargo()))
                .map(lista -> new ListaSetorResponseDto(
                        lista.getIdUsuario(),
                        lista.getNome(),
                        lista.getCargo().getCargo()
                ))
                .toList();
        return listaFiltrada;
    }
    @Transactional
    public void indicar(long idFuncionario, String sub){
        long idGestor = Integer.parseInt(sub);
        List<ListaSetorResponseDto> listaDeUsuarios = listaDeFuncionariosDoSetorDoGestor(sub);
        long quantidadeDePessoasComCargoPRR = listaDeUsuarios.stream()
                .filter(lista -> lista.cargo().equals("RESPONSAVEL_PRR"))
                .count();
        if(quantidadeDePessoasComCargoPRR < 2){
            Usuario usuario = usuarioRepository.findById(idFuncionario).get();
            Long cargoAnterior = usuario.getCargo().getIdCargo();
            Cargo cargo = cargoRepository.findById(4L).get();
            usuario.setCargo(cargo);
            usuarioRepository.save(usuario);
            Usuario gestor = usuarioRepository.findById(idGestor).get();
            historicoService.registrarAlteracaoCargo(
                    gestor,
                    usuario,
                    cargoAnterior,
                    cargo.getIdCargo()
            );
        }else {
            throw new QuantidadeException("Quantidade de pessoas com o cargo RESPONSAVEL_PRR não pode ser excedida");
        }
    }
    @Transactional
    public void rollbackIndicacao(long idFuncionario, String sub){
        Optional<Historico> historicoOptional = historicoRepository.findFirstByTabelaAndIdRegistroAndOperacaoOrderByIdDesc(
                "usuario",
                idFuncionario,
                "ATRIBUIR_PRR"
        );
        if(historicoOptional.isPresent()){
            Historico historico = historicoOptional.get();
            long cargoAnterior = historico.getAntes()
                    .get("id_cargo")
                    .asLong();
            Usuario usuario = usuarioRepository.findById(idFuncionario).get();
            Cargo cargo = cargoRepository.findById(cargoAnterior).get();
            usuario.setCargo(cargo);
            usuarioRepository.save(usuario);
            long idGestor = Integer.parseInt(sub);
            Usuario gestor = usuarioRepository.findById(idGestor).get();
            historicoService.voltarAoCargoAntigo(
                    gestor,
                    usuario,
                    usuario.getCargo().getIdCargo(),
                    cargoAnterior
            );
        }else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "histórico de atribuição não encontrado"
            );
        }
    }
}

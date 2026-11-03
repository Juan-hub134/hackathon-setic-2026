package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.HistoricoPaginaResponseDto;
import br.com.hackathon.setic.dto.response.HistoricoResponseDto;
import br.com.hackathon.setic.model.Cargo;
import br.com.hackathon.setic.model.Historico;
import br.com.hackathon.setic.model.Risco;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.CargoRepository;
import br.com.hackathon.setic.repository.HistoricoRepository;
import br.com.hackathon.setic.repository.RiscoRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoConsultaService {
    private static final String CSGR = "CSGR";
    private static final String CGGE = "CGGE";
    private static final String GESTOR_RISCO = "GESTOR_RISCO";
    private static final String RESPONSAVEL_PRR = "RESPONSAVEL_PRR";
    private static final int TAMANHO_MAXIMO_PAGINA = 100;

    private final HistoricoRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RiscoRepository riscoRepository;
    private final CargoRepository cargoRepository;

    public HistoricoConsultaService(
            HistoricoRepository historicoRepository,
            UsuarioRepository usuarioRepository,
            RiscoRepository riscoRepository,
            CargoRepository cargoRepository
    ) {
        this.historicoRepository = historicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.riscoRepository = riscoRepository;
        this.cargoRepository = cargoRepository;
    }

    @Transactional(readOnly = true)
    public List<HistoricoResponseDto> listarPorRegistro(String tabela, long idRegistro, String sub) {
        Usuario solicitante = buscarUsuarioSolicitante(sub);
        String cargoSolicitante = obterCargo(solicitante);
        validarCargoComAcesso(cargoSolicitante);
        validarTabelaDeRegistro(tabela);

        if (cargoSolicitante.equals(RESPONSAVEL_PRR) && !tabela.equals("risco")) {
            throw naoEncontrado();
        }

        List<Historico> historicos =
                historicoRepository.findAllByTabelaAndIdRegistroOrderByAlteradoEmDescIdDesc(tabela, idRegistro);
        if (historicos.isEmpty()) {
            throw naoEncontrado();
        }

        if (!temAcessoGlobal(cargoSolicitante) && !registroPertenceAoSetor(tabela, idRegistro, solicitante)) {
            throw naoEncontrado();
        }

        List<HistoricoResponseDto> resposta = new ArrayList<>();
        for (Historico historico : historicos) {
            resposta.add(converterParaDto(historico));
        }
        return resposta;
    }

    @Transactional(readOnly = true)
    public HistoricoPaginaResponseDto listarTodos(
            String tabela,
            Long idRegistro,
            int page,
            int size,
            String sub
    ) {
        Usuario solicitante = buscarUsuarioSolicitante(sub);
        String cargoSolicitante = obterCargo(solicitante);
        if (!temAcessoGlobal(cargoSolicitante)) {
            throw semPermissao();
        }
        if (page < 0 || size < 1 || size > TAMANHO_MAXIMO_PAGINA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page deve ser maior ou igual a 0 e size deve estar entre 1 e 100"
            );
        }

        PageRequest paginacao = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "alteradoEm")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );
        Page<Historico> resultado = historicoRepository.buscarComFiltros(tabela, idRegistro, paginacao);
        List<HistoricoResponseDto> itens = new ArrayList<>();
        for (Historico historico : resultado.getContent()) {
            itens.add(converterParaDto(historico));
        }

        return new HistoricoPaginaResponseDto(
                itens,
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private Usuario buscarUsuarioSolicitante(String sub) {
        long idUsuario = Long.parseLong(sub);
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        if (usuarioOptional.isEmpty()) {
            throw naoEncontrado();
        }
        return usuarioOptional.get();
    }

    private String obterCargo(Usuario usuario) {
        if (usuario.getCargo() == null || usuario.getCargo().getCargo() == null) {
            throw semPermissao();
        }
        return usuario.getCargo().getCargo();
    }

    private void validarCargoComAcesso(String cargo) {
        if (!temAcessoGlobal(cargo)
                && !cargo.equals(GESTOR_RISCO)
                && !cargo.equals(RESPONSAVEL_PRR)) {
            throw semPermissao();
        }
    }

    private boolean temAcessoGlobal(String cargo) {
        return cargo.equals(CSGR) || cargo.equals(CGGE);
    }

    private void validarTabelaDeRegistro(String tabela) {
        if (!tabela.equals("risco") && !tabela.equals("usuario")) {
            throw naoEncontrado();
        }
    }

    private boolean registroPertenceAoSetor(String tabela, long idRegistro, Usuario solicitante) {
        if (solicitante.getSetor() == null) {
            return false;
        }
        long idSetorSolicitante = solicitante.getSetor().getIdSetor();

        if (tabela.equals("usuario")) {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(idRegistro);
            if (usuarioOptional.isEmpty() || usuarioOptional.get().getSetor() == null) {
                return false;
            }
            return usuarioOptional.get().getSetor().getIdSetor() == idSetorSolicitante;
        }

        Optional<Risco> riscoOptional = riscoRepository.findById(idRegistro);
        if (riscoOptional.isEmpty()
                || riscoOptional.get().getUsuario() == null
                || riscoOptional.get().getUsuario().getSetor() == null) {
            return false;
        }
        return riscoOptional.get().getUsuario().getSetor().getIdSetor() == idSetorSolicitante;
    }

    private HistoricoResponseDto converterParaDto(Historico historico) {
        HistoricoResponseDto.QuemDto quem;
        String nomeAutor;
        if (historico.getUsuario() == null) {
            quem = new HistoricoResponseDto.QuemDto(null, "usuário removido");
            nomeAutor = "Usuário removido";
        } else {
            quem = new HistoricoResponseDto.QuemDto(
                    historico.getUsuario().getIdUsuario(),
                    historico.getUsuario().getNome()
            );
            nomeAutor = historico.getUsuario().getNome();
        }

        return new HistoricoResponseDto(
                historico.getId(),
                historico.getTabela(),
                historico.getIdRegistro(),
                historico.getOperacao(),
                criarDescricao(historico, nomeAutor),
                quem,
                historico.getAlteradoEm()
        );
    }

    private String criarDescricao(Historico historico, String nomeAutor) {
        if (historico.getOperacao().equals("ATRIBUIR_PRR")) {
            return nomeAutor
                    + " indicou "
                    + nomeUsuario(historico.getIdRegistro())
                    + " como responsável (cargo anterior: "
                    + nomeCargo(historico.getAntes())
                    + ").";
        }
        if (historico.getOperacao().equals("rollback")) {
            return nomeAutor
                    + " desfez a indicação de "
                    + nomeUsuario(historico.getIdRegistro())
                    + " (cargo restaurado: "
                    + nomeCargo(historico.getDepois())
                    + ").";
        }
        if (historico.getOperacao().equals("REGISTRAR_RISCO")) {
            return nomeAutor + " registrou o risco " + historico.getIdRegistro() + ".";
        }
        return nomeAutor + " alterou um registro da tabela " + historico.getTabela() + ".";
    }

    private String nomeUsuario(long idUsuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        if (usuarioOptional.isEmpty()) {
            return "usuário removido";
        }
        return usuarioOptional.get().getNome();
    }

    private String nomeCargo(JsonNode dadosCargo) {
        if (dadosCargo == null
                || !dadosCargo.has("id_cargo")
                || !dadosCargo.get("id_cargo").isNumber()) {
            return "não identificado";
        }

        Optional<Cargo> cargoOptional = cargoRepository.findById(dadosCargo.get("id_cargo").asLong());
        if (cargoOptional.isEmpty()) {
            return "não identificado";
        }
        return cargoOptional.get().getCargo();
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "histórico não encontrado");
    }

    private ResponseStatusException semPermissao() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "sem permissão para consultar o histórico");
    }
}

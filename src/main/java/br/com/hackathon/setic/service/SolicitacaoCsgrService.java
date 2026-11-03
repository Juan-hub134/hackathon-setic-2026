package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.request.SolicitacaoCsgrRequestDto;
import br.com.hackathon.setic.dto.response.EscalaResponseDto;
import br.com.hackathon.setic.dto.response.SolicitacaoCsgrResponseDto;
import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.PlanoRespostaRepository;
import br.com.hackathon.setic.repository.SolicitacaoCsgrRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitacaoCsgrService {
    private final SolicitacaoCsgrRepository solicitacaoRepository;
    private final PlanoRespostaRepository planoRespostaRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitacaoCsgrService(SolicitacaoCsgrRepository solicitacaoRepository,
                                  PlanoRespostaRepository planoRespostaRepository,
                                  UsuarioRepository usuarioRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.planoRespostaRepository = planoRespostaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public SolicitacaoCsgrResponseDto solicitar(Long idResposta, String motivo, String sub) {
        Usuario solicitante = buscarUsuario(sub);
        exigirCargo(solicitante, "GESTOR_RISCO");

        PlanoResposta planoResposta = planoRespostaRepository.findById(idResposta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRR não encontrado."));
        Risco risco = planoResposta.getContexto().getRisco();
        if (risco.getUsuario().getSetor().getIdSetor() != solicitante.getSetor().getIdSetor()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "A solicitação só pode ser aberta pelo titular do setor responsável pelo risco.");
        }
        if (planoResposta.getStatus() != StatusPrr.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este PRR já foi avaliado e não aceita uma nova solicitação.");
        }
        if (solicitacaoRepository.existsByPlanoRespostaIdRespostaAndStatus(
                idResposta, StatusSolicitacaoCsgr.PENDENTE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma solicitação direta pendente para este PRR.");
        }

        SolicitacaoCsgr solicitacao = new SolicitacaoCsgr();
        solicitacao.setPlanoResposta(planoResposta);
        solicitacao.setUsuarioSolicitante(solicitante);
        solicitacao.setMotivo(motivo.trim());
        solicitacao.setSolicitadoEm(LocalDateTime.now());
        solicitacao.setStatus(StatusSolicitacaoCsgr.PENDENTE);
        return toResponse(solicitacaoRepository.save(solicitacao));
    }

    @Transactional
    public List<SolicitacaoCsgrResponseDto> listarPendentes(String sub) {
        Usuario usuario = buscarUsuario(sub);
        exigirCargo(usuario, "CSGR");
        return solicitacaoRepository.findByStatusOrderBySolicitadoEmAsc(StatusSolicitacaoCsgr.PENDENTE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<SolicitacaoCsgrResponseDto> listarMinhas(String sub) {
        Usuario usuario = buscarUsuario(sub);
        exigirCargo(usuario, "GESTOR_RISCO");
        return solicitacaoRepository.findByUsuarioSolicitanteIdUsuarioOrderBySolicitadoEmDesc(
                        usuario.getIdUsuario())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Usuario buscarUsuario(String sub) {
        long idUsuario = Long.parseLong(sub);
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    private void exigirCargo(Usuario usuario, String cargoEsperado) {
        if (usuario.getCargo() == null || !cargoEsperado.equals(usuario.getCargo().getCargo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seu cargo não permite esta operação.");
        }
    }

    private SolicitacaoCsgrResponseDto toResponse(SolicitacaoCsgr solicitacao) {
        PlanoResposta prr = solicitacao.getPlanoResposta();
        Risco risco = prr.getContexto().getRisco();
        return new SolicitacaoCsgrResponseDto(
                solicitacao.getIdSolicitacao(),
                prr.getIdResposta(),
                risco.getIdRisco(),
                risco.getDescricao(),
                risco.getUsuario().getSetor().getUnidade(),
                new EscalaResponseDto(
                        risco.getEscalaProbabilidade().getNota(),
                        risco.getEscalaProbabilidade().getRotulo()),
                new EscalaResponseDto(
                        risco.getEscalaImpacto().getNota(),
                        risco.getEscalaImpacto().getRotulo()),
                risco.getNivel(),
                solicitacao.getUsuarioSolicitante().getNome(),
                solicitacao.getMotivo(),
                solicitacao.getSolicitadoEm(),
                solicitacao.getAtendidoEm(),
                solicitacao.getStatus(),
                prr.getStatus(),
                prr.getContexto().getEscopo(),
                prr.getTratamento().getRotulo(),
                prr.getParametro().getRotulo(),
                prr.getNomeResponsaveis(),
                prr.getControlesExistentes(),
                prr.getAcaoProposta(),
                prr.getMetricas()
        );
    }
}

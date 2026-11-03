package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.request.PlanoRespostaRequestDto;
import br.com.hackathon.setic.dto.response.PlanoRespostaResponseDto;
import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlanoRespostaService {
    private final PlanoRespostaRepository planoRespostaRepository;
    private final ContextoRepository contextoRepository;
    private final TratamentoRiscosRepository tratamentoRiscosRepository;
    private final ParametrosRepository parametrosRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoService historicoService;

    public PlanoRespostaService(PlanoRespostaRepository planoRespostaRepository,
                                ContextoRepository contextoRepository,
                                TratamentoRiscosRepository tratamentoRiscosRepository,
                                ParametrosRepository parametrosRepository,
                                UsuarioRepository usuarioRepository,
                                HistoricoService historicoService) {
        this.planoRespostaRepository = planoRespostaRepository;
        this.contextoRepository = contextoRepository;
        this.tratamentoRiscosRepository = tratamentoRiscosRepository;
        this.parametrosRepository = parametrosRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoService = historicoService;
    }

    @Transactional
    public PlanoRespostaResponseDto criarPlanoResposta(PlanoRespostaRequestDto dto, String sub) {
        long idUsuario = Long.parseLong(sub);
        Usuario usuario = usuarioRepository.findById(idUsuario).get();

        // Buscar o contexto
        Contexto contexto = contextoRepository.findById(dto.idContexto()).get();

        // Verificar se o usuário tem permissão (GESTOR_RISCO do mesmo setor)
        Risco risco = contexto.getRisco();
        if (risco.getUsuario().getSetor().getIdSetor() != usuario.getSetor().getIdSetor()) {
            throw new RuntimeException("Você não tem permissão para criar PRR para este contexto");
        }

        // Verificar se já existe PRR para este contexto
        Optional<PlanoResposta> prrExistente = planoRespostaRepository.findByContextoId(dto.idContexto());
        if (prrExistente.isPresent()) {
            throw new RuntimeException("Já existe um Plano de Resposta criado para este contexto");
        }

        // Buscar tratamento e parametro
        TratamentoRiscos tratamento = tratamentoRiscosRepository.findById(dto.idTratamento()).get();
        Parametros parametro = parametrosRepository.findById(dto.idParametro()).get();

        // Criar o PRR
        PlanoResposta planoResposta = new PlanoResposta();
        planoResposta.setContexto(contexto);
        planoResposta.setTratamento(tratamento);
        planoResposta.setParametro(parametro);
        planoResposta.setNomeResponsaveis(dto.nomeResponsaveis());
        planoResposta.setControlesExistentes(dto.controlesExistentes());
        planoResposta.setAcaoProposta(dto.acaoProposta());
        planoResposta.setMetricas(dto.metricas());

        planoRespostaRepository.save(planoResposta);

        // Registrar no histórico
        historicoService.registrarPrrNovo(usuario, planoResposta);

        return buildPlanoRespostaResponseDto(planoResposta);
    }

    public PlanoRespostaResponseDto buscarPrrPorContexto(Long idContexto, String sub) {
        long idUsuario = Long.parseLong(sub);
        Usuario usuario = usuarioRepository.findById(idUsuario).get();

        Contexto contexto = contextoRepository.findById(idContexto).get();
        Risco risco = contexto.getRisco();

        // Verificar permissão por setor (exceto CGGE e CSGR)
        String cargo = usuario.getCargo().getCargo();
        if (!cargo.equals("CGGE") && !cargo.equals("CSGR")) {
            if (risco.getUsuario().getSetor().getIdSetor() != usuario.getSetor().getIdSetor()) {
                throw new RuntimeException("Você não tem permissão para visualizar este PRR");
            }
        }

        PlanoResposta prr = planoRespostaRepository.findByContextoId(idContexto)
                .orElseThrow(() -> new RuntimeException("Plano de Resposta não encontrado para este contexto"));

        return buildPlanoRespostaResponseDto(prr);
    }

    @Transactional
    public PlanoRespostaResponseDto atualizarPrr(Long idPrr, PlanoRespostaRequestDto dto, String sub) {
        long idUsuario = Long.parseLong(sub);
        Usuario usuario = usuarioRepository.findById(idUsuario).get();

        PlanoResposta prr = planoRespostaRepository.findById(idPrr).get();

        // Verificar permissão
        Risco risco = prr.getContexto().getRisco();
        if (risco.getUsuario().getSetor().getIdSetor() != usuario.getSetor().getIdSetor()) {
            throw new RuntimeException("Você não tem permissão para atualizar este PRR");
        }

        // Atualizar os campos
        TratamentoRiscos tratamento = tratamentoRiscosRepository.findById(dto.idTratamento()).get();
        Parametros parametro = parametrosRepository.findById(dto.idParametro()).get();

        prr.setTratamento(tratamento);
        prr.setParametro(parametro);
        prr.setNomeResponsaveis(dto.nomeResponsaveis());
        prr.setControlesExistentes(dto.controlesExistentes());
        prr.setAcaoProposta(dto.acaoProposta());
        prr.setMetricas(dto.metricas());

        planoRespostaRepository.save(prr);

        return buildPlanoRespostaResponseDto(prr);
    }

    private PlanoRespostaResponseDto buildPlanoRespostaResponseDto(PlanoResposta prr) {
        return new PlanoRespostaResponseDto(
                prr.getIdResposta(),
                prr.getContexto().getIdContexto(),
                prr.getTratamento().getRotulo(),
                prr.getParametro().getRotulo(),
                prr.getNomeResponsaveis(),
                prr.getControlesExistentes(),
                prr.getAcaoProposta(),
                prr.getMetricas(),
                prr.getStatus()
        );
    }
}

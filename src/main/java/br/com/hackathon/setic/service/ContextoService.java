package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.request.ContextoRequestDto;
import br.com.hackathon.setic.dto.response.ContextoResponseDto;
import br.com.hackathon.setic.model.Contexto;
import br.com.hackathon.setic.model.ContextoEtapa;
import br.com.hackathon.setic.model.Risco;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.ContextoEtapaRepository;
import br.com.hackathon.setic.repository.ContextoRepository;
import br.com.hackathon.setic.repository.RiscoRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContextoService {
    private final ContextoRepository contextoRepository;
    private final ContextoEtapaRepository contextoEtapaRepository;
    private final RiscoRepository riscoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoService historicoService;

    public ContextoService(ContextoRepository contextoRepository,
                           ContextoEtapaRepository contextoEtapaRepository,
                           RiscoRepository riscoRepository,
                           UsuarioRepository usuarioRepository,
                           HistoricoService historicoService) {
        this.contextoRepository = contextoRepository;
        this.contextoEtapaRepository = contextoEtapaRepository;
        this.riscoRepository = riscoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoService = historicoService;
    }

    @Transactional
    public ContextoResponseDto criarContexto(ContextoRequestDto dto, String sub) {
        long idUsuario = Long.parseLong(sub);
        Usuario usuario = usuarioRepository.findById(idUsuario).get();

        // Buscar o risco
        Risco risco = riscoRepository.findById(dto.idRisco()).get();

        // Verificar se o risco pertence ao setor do usuário (GESTOR_RISCO)
        if (risco.getUsuario().getSetor().getIdSetor() != usuario.getSetor().getIdSetor()) {
            throw new RuntimeException("Você não tem permissão para criar contexto para este risco");
        }

        // Verificar se já existe contexto para este risco
        Optional<Contexto> contextoExistente = contextoRepository.findByRiscoId(dto.idRisco());
        if (contextoExistente.isPresent()) {
            throw new RuntimeException("Já existe um contexto criado para este risco");
        }

        // Criar o contexto
        Contexto contexto = new Contexto();
        contexto.setRisco(risco);
        contexto.setEscopo(dto.escopo());
        contexto.setCriadoEm(LocalDateTime.now());
        contextoRepository.save(contexto);

        // Criar as etapas
        List<ContextoEtapa> etapas = dto.etapas().stream()
                .map(etapaDto -> {
                    ContextoEtapa etapa = new ContextoEtapa();
                    etapa.setContexto(contexto);
                    etapa.setEtapa(etapaDto.etapa());
                    etapa.setPrazo(etapaDto.prazo());
                    etapa.setStatus(false);
                    return etapa;
                })
                .toList();
        contextoEtapaRepository.saveAll(etapas);

        // Registrar no histórico
        historicoService.registrarContextoNovo(usuario, contexto);

        return buildContextoResponseDto(contexto, etapas);
    }

    public ContextoResponseDto buscarContextoPorRisco(Long idRisco, String sub) {
        long idUsuario = Long.parseLong(sub);
        Usuario usuario = usuarioRepository.findById(idUsuario).get();

        Risco risco = riscoRepository.findById(idRisco).get();

        // Verificar permissão por setor (exceto CGGE e CSGR que veem tudo)
        String cargo = usuario.getCargo().getCargo();
        if (!cargo.equals("CGGE") && !cargo.equals("CSGR")) {
            if (risco.getUsuario().getSetor().getIdSetor() != usuario.getSetor().getIdSetor()) {
                throw new RuntimeException("Você não tem permissão para visualizar este contexto");
            }
        }

        Contexto contexto = contextoRepository.findByRiscoId(idRisco)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contexto não encontrado para este risco"
                ));

        List<ContextoEtapa> etapas = contextoEtapaRepository.findByContextoId(contexto.getIdContexto());

        return buildContextoResponseDto(contexto, etapas);
    }

    @Transactional
    public void concluirEtapa(Long idEtapa, String sub) {
        long idUsuario = Long.parseLong(sub);
        Usuario usuario = usuarioRepository.findById(idUsuario).get();

        ContextoEtapa etapa = contextoEtapaRepository.findById(idEtapa).get();

        // Verificar permissão
        Risco risco = etapa.getContexto().getRisco();
        if (risco.getUsuario().getSetor().getIdSetor() != usuario.getSetor().getIdSetor()) {
            throw new RuntimeException("Você não tem permissão para concluir esta etapa");
        }

        etapa.setStatus(true);
        etapa.setConcluidoEm(java.time.LocalDate.now());
        contextoEtapaRepository.save(etapa);
    }

    private ContextoResponseDto buildContextoResponseDto(Contexto contexto, List<ContextoEtapa> etapas) {
        List<ContextoResponseDto.EtapaResponseDto> etapasDto = etapas.stream()
                .map(e -> new ContextoResponseDto.EtapaResponseDto(
                        e.getIdContextoEtapa(),
                        e.getEtapa(),
                        e.getPrazo(),
                        e.getConcluidoEm(),
                        e.getStatus()
                ))
                .toList();

        return new ContextoResponseDto(
                contexto.getIdContexto(),
                contexto.getRisco().getIdRisco(),
                contexto.getEscopo(),
                contexto.getCriadoEm(),
                etapasDto
        );
    }
}

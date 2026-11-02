package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.request.RiscoRequestDto;
import br.com.hackathon.setic.dto.response.RiscoResponseDto;
import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RiscoService {
    private final RiscoRepository riscoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EscalaProbabilidadeRepository escalaProbabilidadeRepository;
    private final EscalaImpactoRepository escalaImpactoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoService historicoService;
    public RiscoService(RiscoRepository riscoRepository, CategoriaRepository categoriaRepository,
                        EscalaImpactoRepository escalaImpactoRepository,
                        EscalaProbabilidadeRepository escalaProbabilidadeRepository,
                        UsuarioRepository usuarioRepository,
                        HistoricoService historicoService) {
        this.riscoRepository = riscoRepository;
        this.categoriaRepository = categoriaRepository;
        this.escalaImpactoRepository = escalaImpactoRepository;
        this.escalaProbabilidadeRepository = escalaProbabilidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoService = historicoService;
    }

    public void registrarRisco(RiscoRequestDto dto, String sub){
        long idPrr = Long.parseLong(sub);
        Risco risco = new Risco();
        Categoria categoria = categoriaRepository.findById(dto.idCategoria()).get();
        risco.setCategoria(categoria);
        risco.setCausa(dto.causa());
        risco.setEvento(dto.evento());
        risco.setImpactoTxt(dto.impactoTxt());
        risco.setObjetivo(dto.objtivo());
        risco.setDescricao(dto.descricao());
        EscalaProbabilidade escalaProbabilidade = escalaProbabilidadeRepository.findById(dto.idProbabilidade()).get();
        risco.setEscalaProbabilidade(escalaProbabilidade);
        EscalaImpacto escalaImpacto = escalaImpactoRepository.findById(dto.idImpacto()).get();
        risco.setEscalaImpacto(escalaImpacto);
        risco.setHorizonteMeses(dto.horizonteMeses());
        Usuario usuario = usuarioRepository.findById(idPrr).get();
        risco.setUsuario(usuario);
        risco.setCriadoPor(usuario.getNome());
        risco.setCriadoEm(LocalDateTime.now());
        long nivel = dto.idImpacto() * dto.idProbabilidade();
        risco.setNivel(nivel);
        riscoRepository.save(risco);
        historicoService.registrarRiscoNovo(usuario, risco);
    }
    public List<RiscoResponseDto> listarRiscos(String sub){
        long idGestor = Long.parseLong(sub);
        Usuario usuario = usuarioRepository.findById(idGestor).get();
        String setor = usuario.getSetor().getUnidade();
        Optional<Risco> riscoOptional = riscoRepository.findById(usuario.getIdUsuario());
        if (riscoOptional.isPresent()) {
            Risco risco  = riscoOptional.get();
        }

    }
}

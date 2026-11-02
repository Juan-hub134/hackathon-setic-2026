package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.request.RiscoRequestDto;
import br.com.hackathon.setic.dto.response.EscalaResponseDto;
import br.com.hackathon.setic.dto.response.RiscoResponseDto;
import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RiscoService {
    private final RiscoRepository riscoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EscalaProbabilidadeRepository escalaProbabilidadeRepository;
    private final EscalaImpactoRepository escalaImpactoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoService historicoService;
    private final SetorRepository setorRepository;
    public RiscoService(RiscoRepository riscoRepository, CategoriaRepository categoriaRepository,
                        EscalaImpactoRepository escalaImpactoRepository,
                        EscalaProbabilidadeRepository escalaProbabilidadeRepository,
                        UsuarioRepository usuarioRepository,
                        HistoricoService historicoService,
                        SetorRepository setorRepository) {
        this.riscoRepository = riscoRepository;
        this.categoriaRepository = categoriaRepository;
        this.escalaImpactoRepository = escalaImpactoRepository;
        this.escalaProbabilidadeRepository = escalaProbabilidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoService = historicoService;
        this.setorRepository = setorRepository;
    }

    public void registrarRisco(RiscoRequestDto dto, String sub){
        long idPrr = Long.parseLong(sub);
        System.out.println("Objetivo recebido: " + dto.objetivo());
        Risco risco = new Risco();
        Categoria categoria = categoriaRepository.findById(dto.idCategoria()).get();
        risco.setCategoria(categoria);
        risco.setCausa(dto.causa());
        risco.setEvento(dto.evento());
        risco.setImpactoTxt(dto.impactoTxt());
        risco.setObjetivo(dto.objetivo());
        String descricao = "Devido a " + risco.getCausa()
                + ", poderá ocorrer " + risco.getEvento()
                + ", levando a " + risco.getImpactoTxt()
                + ", afetando " + risco.getObjetivo() + ".";
        risco.setDescricao(descricao);
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
    public List<RiscoResponseDto> listarRiscosPeloSetorGestor(String sub) {
        long idGestor = Long.parseLong(sub);
        Usuario usuario = usuarioRepository.findById(idGestor).get();
        long idSetor = usuario.getSetor().getIdSetor();
        List<Risco> listaDeRiscoSetor = riscoRepository.buscarRiscoPorSetor(idSetor);
        List<RiscoResponseDto> listaDeRiscoResponseDto = listaDeRiscoSetor.stream()
                .map(m -> {
                    String nomeCategoria = m.getCategoria().getNome();
                    String nomeSetor = m.getUsuario().getSetor().getUnidade();
                    long notaProbabilidade = m.getEscalaProbabilidade().getNota();
                    String rotulo = m.getEscalaProbabilidade().getRotulo();
                    EscalaResponseDto escalaProbabilidadeDto = new EscalaResponseDto(notaProbabilidade, rotulo);
                    long notaImpacto = m.getEscalaImpacto().getNota();
                    String rotuloImpacto = m.getEscalaImpacto().getRotulo();
                    EscalaResponseDto escalaImpactoResponseDto = new EscalaResponseDto(notaImpacto, rotuloImpacto);
                    return new RiscoResponseDto(
                            m.getIdRisco(),
                            nomeSetor,
                            nomeCategoria,
                            m.getDescricao(),
                            escalaProbabilidadeDto,
                            escalaImpactoResponseDto,
                            m.getNivel(),
                            m.getHorizonteMeses(),
                            m.getCriadoPor(),
                            m.getCriadoEm()
                    );
                })
                .toList();
        return listaDeRiscoResponseDto;
    }
    public List<RiscoResponseDto> listarRiscosDeTodosOsSetores() {
        List<Risco> listaDeRiscos = riscoRepository.findAll();
        List<RiscoResponseDto> listaRiscoResponseDto = listaDeRiscos.stream()
                .map(m -> {
                    String nomeCategoria = m.getCategoria().getNome();
                    String nomeSetor = m.getUsuario().getSetor().getUnidade();
                    long notaProbabilidade = m.getEscalaProbabilidade().getNota();
                    String rotulo = m.getEscalaProbabilidade().getRotulo();
                    EscalaResponseDto escalaProbabilidadeDto = new EscalaResponseDto(notaProbabilidade, rotulo);
                    long notaImpacto = m.getEscalaImpacto().getNota();
                    String rotuloImpacto = m.getEscalaImpacto().getRotulo();
                    EscalaResponseDto escalaImpactoResponseDto = new EscalaResponseDto(notaImpacto, rotuloImpacto);
                    return new RiscoResponseDto(
                            m.getIdRisco(),
                            nomeSetor,
                            nomeCategoria,
                            m.getDescricao(),
                            escalaProbabilidadeDto,
                            escalaImpactoResponseDto,
                            m.getNivel(),
                            m.getHorizonteMeses(),
                            m.getCriadoPor(),
                            m.getCriadoEm()
                    );
                })
                .toList();
        return listaRiscoResponseDto;
    }

    public RiscoResponseDto filtrarTodosOsRiscos(long id){
        Risco risco = riscoRepository.findById(id).get();
        long idUsuario = risco.getUsuario().getIdUsuario();
        String setor = risco.getUsuario().getSetor().getUnidade();
        String categoria =  risco.getCategoria().getNome();
        long notaProbabilidade = risco.getEscalaProbabilidade().getNota();
        String rotulo = risco.getEscalaProbabilidade().getRotulo();
        EscalaResponseDto escalaProbabilidade = new EscalaResponseDto(notaProbabilidade, rotulo);
        long notaImpacto = risco.getEscalaImpacto().getNota();
        String rotuloImpacto = risco.getEscalaImpacto().getRotulo();
        EscalaResponseDto escalaImpacto =  new EscalaResponseDto(notaImpacto, rotuloImpacto);
        return new RiscoResponseDto(risco.getIdRisco(),
                setor,
                categoria,
                risco.getDescricao(),
                escalaProbabilidade,
                escalaImpacto,
                risco.getNivel(),
                risco.getHorizonteMeses(),
                risco.getCriadoPor(),
                risco.getCriadoEm()
                );
    }
}

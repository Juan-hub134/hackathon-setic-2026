package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.request.ParecerRequestDto;
import br.com.hackathon.setic.dto.response.ParecerResponseDto;
import br.com.hackathon.setic.model.ParecerPrr;
import br.com.hackathon.setic.model.PlanoResposta;
import br.com.hackathon.setic.model.StatusPrr;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.ParecerPrrRepository;
import br.com.hackathon.setic.repository.PlanoRespostaRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParecerPrrService {
    private final ParecerPrrRepository parecerPrrRepository;
    private final PlanoRespostaRepository planoRespostaRepository;
    private final UsuarioRepository usuarioRepository;

    public ParecerPrrService(ParecerPrrRepository parecerPrrRepository,
                             PlanoRespostaRepository planoRespostaRepository,
                             UsuarioRepository usuarioRepository) {
        this.parecerPrrRepository = parecerPrrRepository;
        this.planoRespostaRepository = planoRespostaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ParecerResponseDto avaliarPrr(ParecerRequestDto dto, String sub) {
        long idUsuario = Long.parseLong(sub);
        Usuario avaliador = usuarioRepository.findById(idUsuario).get();

        // Verificar se é CSGR
        String cargo = avaliador.getCargo().getCargo();
        if (!cargo.equals("CSGR")) {
            throw new RuntimeException("Apenas membros da CSGR podem avaliar PRR");
        }

        // Buscar o PRR
        PlanoResposta prr = planoRespostaRepository.findById(dto.idResposta()).get();

        // Verificar se está pendente
        if (prr.getStatus() != StatusPrr.PENDENTE) {
            throw new RuntimeException("Este PRR já foi avaliado. Status atual: " + prr.getStatus());
        }

        // Validar status
        if (dto.statusResultado() == StatusPrr.PENDENTE) {
            throw new RuntimeException("Status de avaliação deve ser APROVADO ou REPROVADO");
        }

        // Criar o parecer
        ParecerPrr parecer = new ParecerPrr();
        parecer.setPlanoResposta(prr);
        parecer.setUsuarioAvaliador(avaliador);
        parecer.setStatusResultado(dto.statusResultado());
        parecer.setParecerTexto(dto.parecerTexto());
        parecer.setAvaliadoEm(LocalDateTime.now());
        parecerPrrRepository.save(parecer);

        // Atualizar status do PRR
        prr.setStatus(dto.statusResultado());
        planoRespostaRepository.save(prr);

        return new ParecerResponseDto(
                parecer.getIdParecer(),
                prr.getIdResposta(),
                avaliador.getNome(),
                parecer.getStatusResultado(),
                parecer.getParecerTexto(),
                parecer.getAvaliadoEm()
        );
    }

    public List<ParecerResponseDto> buscarHistoricoParecesPorPrr(Long idResposta) {
        List<ParecerPrr> pareceres = parecerPrrRepository.findByPlanoRespostaIdOrderByAvaliadoEmDesc(idResposta);

        return pareceres.stream()
                .map(p -> new ParecerResponseDto(
                        p.getIdParecer(),
                        p.getPlanoResposta().getIdResposta(),
                        p.getUsuarioAvaliador().getNome(),
                        p.getStatusResultado(),
                        p.getParecerTexto(),
                        p.getAvaliadoEm()
                ))
                .toList();
    }

    public List<PlanoResposta> listarPrrsPendentes() {
        return planoRespostaRepository.findAll().stream()
                .filter(prr -> prr.getStatus() == StatusPrr.PENDENTE)
                .toList();
    }
}

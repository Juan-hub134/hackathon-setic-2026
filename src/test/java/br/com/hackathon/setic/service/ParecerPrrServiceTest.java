package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.request.ParecerRequestDto;
import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.ParecerPrrRepository;
import br.com.hackathon.setic.repository.PlanoRespostaRepository;
import br.com.hackathon.setic.repository.SolicitacaoCsgrRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParecerPrrServiceTest {
    private final ParecerPrrRepository parecerRepository = mock(ParecerPrrRepository.class);
    private final PlanoRespostaRepository planoRespostaRepository = mock(PlanoRespostaRepository.class);
    private final SolicitacaoCsgrRepository solicitacaoRepository = mock(SolicitacaoCsgrRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final ParecerPrrService service = new ParecerPrrService(
            parecerRepository, planoRespostaRepository, solicitacaoRepository, usuarioRepository);

    @Test
    void completesDirectRequestAfterCsgRRegistersOpinion() {
        Usuario avaliador = usuario(45, "CSGR");
        PlanoResposta prr = new PlanoResposta();
        prr.setIdResposta(31L);
        prr.setStatus(StatusPrr.PENDENTE);
        SolicitacaoCsgr solicitacao = new SolicitacaoCsgr();
        solicitacao.setStatus(StatusSolicitacaoCsgr.PENDENTE);
        when(usuarioRepository.findById(45L)).thenReturn(Optional.of(avaliador));
        when(planoRespostaRepository.findById(31L)).thenReturn(Optional.of(prr));
        when(solicitacaoRepository.findFirstByPlanoRespostaIdRespostaAndStatusOrderBySolicitadoEmAsc(
                31L, StatusSolicitacaoCsgr.PENDENTE)).thenReturn(Optional.of(solicitacao));
        when(parecerRepository.save(any(ParecerPrr.class))).thenAnswer(invocation -> {
            ParecerPrr parecer = invocation.getArgument(0);
            parecer.setIdParecer(9L);
            return parecer;
        });

        var response = service.avaliarPrr(
                new ParecerRequestDto(31L, StatusPrr.APROVADO, "Análise concluída."), "45");

        assertThat(response.statusResultado()).isEqualTo(StatusPrr.APROVADO);
        assertThat(prr.getStatus()).isEqualTo(StatusPrr.APROVADO);
        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacaoCsgr.ATENDIDA);
        assertThat(solicitacao.getUsuarioAvaliador()).isSameAs(avaliador);
        verify(solicitacaoRepository).save(solicitacao);
    }

    @Test
    void refusesOpinionWithoutTitularRequest() {
        Usuario avaliador = usuario(45, "CSGR");
        PlanoResposta prr = new PlanoResposta();
        prr.setIdResposta(31L);
        prr.setStatus(StatusPrr.PENDENTE);
        when(usuarioRepository.findById(45L)).thenReturn(Optional.of(avaliador));
        when(planoRespostaRepository.findById(31L)).thenReturn(Optional.of(prr));
        when(solicitacaoRepository.findFirstByPlanoRespostaIdRespostaAndStatusOrderBySolicitadoEmAsc(
                31L, StatusSolicitacaoCsgr.PENDENTE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.avaliarPrr(
                new ParecerRequestDto(31L, StatusPrr.APROVADO, "Análise."), "45"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
        verify(parecerRepository, never()).save(any());
    }

    private Usuario usuario(long id, String cargoNome) {
        Cargo cargo = new Cargo();
        cargo.setCargo(cargoNome);
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNome("Membro CSGR");
        usuario.setCargo(cargo);
        return usuario;
    }
}

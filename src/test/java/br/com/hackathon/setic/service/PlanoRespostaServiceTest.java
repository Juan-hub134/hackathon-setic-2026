package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.request.PlanoRespostaRequestDto;
import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PlanoRespostaServiceTest {
    private final PlanoRespostaRepository planoRepository = mock(PlanoRespostaRepository.class);
    private final ContextoRepository contextoRepository = mock(ContextoRepository.class);
    private final TratamentoRiscosRepository tratamentoRepository = mock(TratamentoRiscosRepository.class);
    private final ParametrosRepository parametrosRepository = mock(ParametrosRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final SolicitacaoCsgrRepository solicitacaoRepository = mock(SolicitacaoCsgrRepository.class);
    private final PlanoRespostaService service = new PlanoRespostaService(
            planoRepository,
            contextoRepository,
            tratamentoRepository,
            parametrosRepository,
            usuarioRepository,
            solicitacaoRepository,
            mock(HistoricoService.class)
    );

    @Test
    void preventsPlanEditsWhileDirectCsgRRequestIsPending() {
        Setor setor = new Setor();
        setor.setIdSetor(7);
        Usuario titular = new Usuario();
        titular.setIdUsuario(11);
        titular.setSetor(setor);

        Usuario registrante = new Usuario();
        registrante.setSetor(setor);
        Risco risco = new Risco();
        risco.setUsuario(registrante);
        Contexto contexto = new Contexto();
        contexto.setRisco(risco);
        PlanoResposta prr = new PlanoResposta();
        prr.setIdResposta(31L);
        prr.setContexto(contexto);

        when(usuarioRepository.findById(11L)).thenReturn(Optional.of(titular));
        when(planoRepository.findById(31L)).thenReturn(Optional.of(prr));
        when(solicitacaoRepository.existsByPlanoRespostaIdRespostaAndStatus(
                31L, StatusSolicitacaoCsgr.PENDENTE)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizarPrr(
                31L,
                new PlanoRespostaRequestDto(1L, 1L, 1L, "Equipe", "Controles", "Ação", "Métrica"),
                "11"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        verify(tratamentoRepository, never()).findById(any());
        verify(planoRepository, never()).save(prr);
    }
}

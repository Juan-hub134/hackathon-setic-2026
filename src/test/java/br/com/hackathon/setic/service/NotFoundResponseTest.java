package br.com.hackathon.setic.service;

import br.com.hackathon.setic.model.Cargo;
import br.com.hackathon.setic.model.Contexto;
import br.com.hackathon.setic.model.Risco;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.*;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.ThrowableAssert;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotFoundResponseTest {
    @Test
    void missingContextReturnsNotFound() {
        ContextoRepository contextoRepository = mock(ContextoRepository.class);
        RiscoRepository riscoRepository = mock(RiscoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario user = userWithRole("CGGE");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        when(riscoRepository.findById(10L)).thenReturn(Optional.of(new Risco()));
        when(contextoRepository.findByRiscoId(10L)).thenReturn(Optional.empty());

        ContextoService service = new ContextoService(
                contextoRepository,
                mock(ContextoEtapaRepository.class),
                riscoRepository,
                usuarioRepository,
                mock(HistoricoService.class)
        );

        assertNotFound(() -> service.buscarContextoPorRisco(10L, "1"), "Contexto não encontrado");
    }

    @Test
    void missingResponsePlanReturnsNotFound() {
        PlanoRespostaRepository planoRepository = mock(PlanoRespostaRepository.class);
        ContextoRepository contextoRepository = mock(ContextoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Contexto contexto = new Contexto();
        contexto.setRisco(new Risco());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(userWithRole("CGGE")));
        when(contextoRepository.findById(20L)).thenReturn(Optional.of(contexto));
        when(planoRepository.findByContextoId(20L)).thenReturn(Optional.empty());

        PlanoRespostaService service = new PlanoRespostaService(
                planoRepository,
                contextoRepository,
                mock(TratamentoRiscosRepository.class),
                mock(ParametrosRepository.class),
                usuarioRepository,
                mock(SolicitacaoCsgrRepository.class),
                mock(HistoricoService.class)
        );

        assertNotFound(() -> service.buscarPrrPorContexto(20L, "1"), "Plano de Resposta não encontrado");
    }

    private Usuario userWithRole(String role) {
        Cargo cargo = new Cargo();
        cargo.setCargo(role);
        Usuario user = new Usuario();
        user.setCargo(cargo);
        return user;
    }

    private void assertNotFound(ThrowableAssert.ThrowingCallable operation, String reason) {
        assertThatThrownBy(operation)
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> {
                    ResponseStatusException responseError = (ResponseStatusException) error;
                    assertThat(responseError.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseError.getReason()).contains(reason);
                });
    }
}

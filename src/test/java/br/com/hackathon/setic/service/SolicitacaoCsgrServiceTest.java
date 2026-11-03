package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.SolicitacaoCsgrResponseDto;
import br.com.hackathon.setic.model.*;
import br.com.hackathon.setic.repository.PlanoRespostaRepository;
import br.com.hackathon.setic.repository.SolicitacaoCsgrRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitacaoCsgrServiceTest {
    private final SolicitacaoCsgrRepository solicitacaoRepository = mock(SolicitacaoCsgrRepository.class);
    private final PlanoRespostaRepository planoRespostaRepository = mock(PlanoRespostaRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final SolicitacaoCsgrService service = new SolicitacaoCsgrService(
            solicitacaoRepository, planoRespostaRepository, usuarioRepository);

    private Usuario titular;
    private PlanoResposta prr;

    @BeforeEach
    void setUp() {
        Setor setor = new Setor();
        setor.setIdSetor(7);
        setor.setUnidade("Tecnologia");

        Cargo cargoTitular = new Cargo();
        cargoTitular.setCargo("GESTOR_RISCO");
        titular = new Usuario();
        titular.setIdUsuario(11);
        titular.setNome("Titular de teste");
        titular.setSetor(setor);
        titular.setCargo(cargoTitular);

        Usuario registrante = new Usuario();
        registrante.setSetor(setor);
        Risco risco = new Risco();
        risco.setIdRisco(23);
        risco.setDescricao("Evento de teste");
        risco.setUsuario(registrante);
        EscalaProbabilidade probabilidade = new EscalaProbabilidade();
        probabilidade.setNota(4);
        probabilidade.setRotulo("Provável");
        EscalaImpacto impacto = new EscalaImpacto();
        impacto.setNota(5);
        impacto.setRotulo("Muito alto");
        risco.setEscalaProbabilidade(probabilidade);
        risco.setEscalaImpacto(impacto);
        risco.setNivel(20);

        Contexto contexto = new Contexto();
        contexto.setRisco(risco);
        contexto.setEscopo("Serviços digitais do setor");
        prr = new PlanoResposta();
        prr.setIdResposta(31L);
        prr.setContexto(contexto);
        prr.setStatus(StatusPrr.PENDENTE);
        TratamentoRiscos tratamento = new TratamentoRiscos();
        tratamento.setRotulo("Mitigar");
        Parametros parametro = new Parametros();
        parametro.setRotulo("Prioridade alta");
        prr.setTratamento(tratamento);
        prr.setParametro(parametro);
        prr.setNomeResponsaveis("Equipe de teste");
        prr.setControlesExistentes("Controle existente");
        prr.setAcaoProposta("Executar ação");
        prr.setMetricas("Meta de teste");

        when(usuarioRepository.findById(11L)).thenReturn(Optional.of(titular));
        when(planoRespostaRepository.findById(31L)).thenReturn(Optional.of(prr));
        when(solicitacaoRepository.save(any(SolicitacaoCsgr.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createsPendingRequestForTitularOfRiskSector() {
        SolicitacaoCsgrResponseDto response = service.solicitar(31L, "  Revisar o tratamento  ", "11");

        assertThat(response.idResposta()).isEqualTo(31L);
        assertThat(response.setor()).isEqualTo("Tecnologia");
        assertThat(response.motivo()).isEqualTo("Revisar o tratamento");
        assertThat(response.status()).isEqualTo(StatusSolicitacaoCsgr.PENDENTE);
        assertThat(response.escopo()).isEqualTo("Serviços digitais do setor");
        assertThat(response.tratamento()).isEqualTo("Mitigar");
        assertThat(response.probabilidade().nota()).isEqualTo(4L);
        assertThat(response.impacto().nota()).isEqualTo(5L);
        assertThat(response.nivel()).isEqualTo(20);
        verify(solicitacaoRepository).save(argThat(request ->
                request.getUsuarioSolicitante() == titular
                        && request.getStatus() == StatusSolicitacaoCsgr.PENDENTE));
    }

    @Test
    void rejectsRequestFromDifferentSector() {
        Setor otherSector = new Setor();
        otherSector.setIdSetor(8);
        titular.setSetor(otherSector);

        assertThatThrownBy(() -> service.solicitar(31L, "Motivo", "11"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
        verify(solicitacaoRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicatePendingRequest() {
        when(solicitacaoRepository.existsByPlanoRespostaIdRespostaAndStatus(
                31L, StatusSolicitacaoCsgr.PENDENTE)).thenReturn(true);

        assertThatThrownBy(() -> service.solicitar(31L, "Motivo", "11"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        verify(solicitacaoRepository, never()).save(any());
    }
}

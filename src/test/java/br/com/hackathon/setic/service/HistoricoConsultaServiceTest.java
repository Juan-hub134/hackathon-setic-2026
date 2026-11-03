package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.HistoricoPaginaResponseDto;
import br.com.hackathon.setic.dto.response.HistoricoResponseDto;
import br.com.hackathon.setic.model.Cargo;
import br.com.hackathon.setic.model.Historico;
import br.com.hackathon.setic.model.Risco;
import br.com.hackathon.setic.model.Setor;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.CargoRepository;
import br.com.hackathon.setic.repository.HistoricoRepository;
import br.com.hackathon.setic.repository.RiscoRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HistoricoConsultaServiceTest {
    private HistoricoRepository historicoRepository;
    private UsuarioRepository usuarioRepository;
    private RiscoRepository riscoRepository;
    private CargoRepository cargoRepository;
    private HistoricoConsultaService service;

    @BeforeEach
    void setUp() {
        historicoRepository = mock(HistoricoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        riscoRepository = mock(RiscoRepository.class);
        cargoRepository = mock(CargoRepository.class);
        service = new HistoricoConsultaService(
                historicoRepository,
                usuarioRepository,
                riscoRepository,
                cargoRepository
        );
    }

    @Test
    void gestorPodeConsultarHistoricoDeRiscoDoProprioSetor() {
        Usuario gestor = usuario(1L, "GESTOR_RISCO", 10L);
        Usuario criador = usuario(2L, "RESPONSAVEL_PRR", 10L);
        Historico historico = historico(3L, "risco", 40L, criador);
        Risco risco = new Risco();
        risco.setUsuario(criador);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(gestor));
        when(historicoRepository.findAllByTabelaAndIdRegistroOrderByAlteradoEmDescIdDesc("risco", 40L))
                .thenReturn(List.of(historico));
        when(riscoRepository.findById(40L)).thenReturn(Optional.of(risco));

        List<HistoricoResponseDto> resposta = service.listarPorRegistro("risco", 40L, "1");

        assertEquals(1, resposta.size());
        assertEquals(2L, resposta.get(0).quem().id());
        assertEquals("RESPONSAVEL_PRR alterou um registro da tabela risco.", resposta.get(0).descricao());
    }

    @Test
    void gestorRecebe404ParaRiscoDeOutroSetor() {
        Usuario gestor = usuario(1L, "GESTOR_RISCO", 10L);
        Usuario criador = usuario(2L, "RESPONSAVEL_PRR", 20L);
        Historico historico = historico(3L, "risco", 40L, criador);
        Risco risco = new Risco();
        risco.setUsuario(criador);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(gestor));
        when(historicoRepository.findAllByTabelaAndIdRegistroOrderByAlteradoEmDescIdDesc("risco", 40L))
                .thenReturn(List.of(historico));
        when(riscoRepository.findById(40L)).thenReturn(Optional.of(risco));

        try {
            service.listarPorRegistro("risco", 40L, "1");
            fail("Esperava HTTP 404 para registro de outro setor");
        } catch (ResponseStatusException exception) {
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        }
    }

    @Test
    void responsavelRecebe404AoConsultarHistoricoDeUsuario() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario(1L, "RESPONSAVEL_PRR", 10L)));

        try {
            service.listarPorRegistro("usuario", 2L, "1");
            fail("Esperava HTTP 404 para histórico de usuário");
        } catch (ResponseStatusException exception) {
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        }
    }

    @Test
    void servidorRecebe403() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario(1L, "SERVIDOR", 10L)));

        try {
            service.listarPorRegistro("risco", 40L, "1");
            fail("Esperava HTTP 403 para cargo sem acesso");
        } catch (ResponseStatusException exception) {
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        }
    }

    @Test
    void csgrPodeConsultarVisaoGeralPaginada() {
        Usuario csgr = usuario(1L, "CSGR", 10L);
        Pageable pageable = PageRequest.of(0, 20);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(csgr));
        when(historicoRepository.buscarComFiltros(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        HistoricoPaginaResponseDto resposta = service.listarTodos(null, null, 0, 20, "1");

        assertEquals(0, resposta.page());
        assertEquals(20, resposta.size());
        assertEquals(0, resposta.totalElementos());
        verify(historicoRepository).buscarComFiltros(isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void respostaNaoExpoeAntesNemDepoisDoHistorico() throws Exception {
        Usuario csgr = usuario(1L, "CSGR", 10L);
        Historico historico = historico(3L, "risco", 40L, csgr);
        ObjectMapper objectMapper = new ObjectMapper();
        historico.setAntes(objectMapper.valueToTree(Map.of("segredo", "valor anterior")));
        historico.setDepois(objectMapper.valueToTree(Map.of("segredo", "valor posterior")));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(csgr));
        when(historicoRepository.findAllByTabelaAndIdRegistroOrderByAlteradoEmDescIdDesc("risco", 40L))
                .thenReturn(List.of(historico));

        String resposta = objectMapper.writeValueAsString(
                service.listarPorRegistro("risco", 40L, "1")
        );

        assertFalse(resposta.contains("\"antes\""));
        assertFalse(resposta.contains("\"depois\""));
        assertFalse(resposta.contains("valor anterior"));
        assertFalse(resposta.contains("valor posterior"));
    }

    private Usuario usuario(long id, String cargoNome, long idSetor) {
        Cargo cargo = new Cargo();
        cargo.setCargo(cargoNome);
        Setor setor = new Setor();
        setor.setIdSetor(idSetor);
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNome(cargoNome);
        usuario.setCargo(cargo);
        usuario.setSetor(setor);
        return usuario;
    }

    private Historico historico(long id, String tabela, long idRegistro, Usuario autor) {
        Historico historico = new Historico();
        historico.setId(id);
        historico.setTabela(tabela);
        historico.setIdRegistro(idRegistro);
        historico.setOperacao("TESTE");
        historico.setUsuario(autor);
        return historico;
    }
}

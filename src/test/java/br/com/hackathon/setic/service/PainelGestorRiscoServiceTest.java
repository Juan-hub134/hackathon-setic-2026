package br.com.hackathon.setic.service;

import br.com.hackathon.setic.model.Cargo;
import br.com.hackathon.setic.model.Historico;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.CargoRepository;
import br.com.hackathon.setic.repository.HistoricoRepository;
import br.com.hackathon.setic.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PainelGestorRiscoServiceTest {
    @Test
    void rollbackRegistraCargoAtualAntesDoRestauradoDepois() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        HistoricoService historicoService = mock(HistoricoService.class);
        CargoRepository cargoRepository = mock(CargoRepository.class);
        HistoricoRepository historicoRepository = mock(HistoricoRepository.class);
        PainelGestorRiscoService service = new PainelGestorRiscoService(
                usuarioRepository,
                historicoService,
                cargoRepository,
                historicoRepository
        );

        Historico historicoOriginal = new Historico();
        historicoOriginal.setAntes(new ObjectMapper().valueToTree(Map.of("id_cargo", 3L)));
        when(historicoRepository.findFirstByTabelaAndIdRegistroAndOperacaoOrderByIdDesc(
                "usuario",
                2L,
                "ATRIBUIR_PRR"
        )).thenReturn(Optional.of(historicoOriginal));

        Cargo cargoAtual = new Cargo();
        cargoAtual.setIdCargo(4L);
        Cargo cargoRestaurado = new Cargo();
        cargoRestaurado.setIdCargo(3L);
        Usuario funcionario = new Usuario();
        funcionario.setCargo(cargoAtual);
        Usuario gestor = new Usuario();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(funcionario));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(gestor));
        when(cargoRepository.findById(3L)).thenReturn(Optional.of(cargoRestaurado));

        service.rollbackIndicacao(2L, "1");

        verify(historicoService).voltarAoCargoAntigo(gestor, funcionario, 4L, 3L);
    }
}

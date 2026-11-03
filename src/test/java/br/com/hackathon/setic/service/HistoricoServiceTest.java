package br.com.hackathon.setic.service;

import br.com.hackathon.setic.model.Categoria;
import br.com.hackathon.setic.model.EscalaImpacto;
import br.com.hackathon.setic.model.EscalaProbabilidade;
import br.com.hackathon.setic.model.Historico;
import br.com.hackathon.setic.model.Risco;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.HistoricoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HistoricoServiceTest {
    @Test
    void riscoNovoRegistraAntesNuloESomenteMetadadosControlados() {
        HistoricoRepository historicoRepository = mock(HistoricoRepository.class);
        HistoricoService service = new HistoricoService(historicoRepository, new ObjectMapper());

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(4L);
        EscalaProbabilidade probabilidade = new EscalaProbabilidade();
        probabilidade.setNota(3);
        EscalaImpacto impacto = new EscalaImpacto();
        impacto.setNota(5);

        Risco risco = new Risco();
        risco.setIdRisco(12L);
        risco.setCategoria(categoria);
        risco.setEscalaProbabilidade(probabilidade);
        risco.setEscalaImpacto(impacto);
        risco.setNivel(15L);
        risco.setHorizonteMeses(12);

        service.registrarRiscoNovo(new Usuario(), risco);

        ArgumentCaptor<Historico> captor = ArgumentCaptor.forClass(Historico.class);
        verify(historicoRepository).save(captor.capture());
        Historico historico = captor.getValue();

        assertNull(historico.getAntes());
        assertFalse(historico.getDepois().has("causa"));
        assertFalse(historico.getDepois().has("evento"));
        assertFalse(historico.getDepois().has("impacto_txt"));
        assertFalse(historico.getDepois().has("objetivo"));
        assertFalse(historico.getDepois().has("descricao"));
    }
}

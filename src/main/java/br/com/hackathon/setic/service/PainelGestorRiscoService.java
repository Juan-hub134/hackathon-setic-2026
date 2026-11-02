package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.response.ListaSetorResponseDto;
import br.com.hackathon.setic.exception.QuantidadeException;
import br.com.hackathon.setic.model.Cargo;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PainelGestorRiscoService {
    private final UsuarioRepository usuarioRepository;
    public PainelGestorRiscoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    public List<ListaSetorResponseDto> listaDeFuncionariosDoSetorDoGestor(String sub){
        long id = Integer.parseInt(sub);
        Usuario usuario = usuarioRepository.findById(id).get();
        long idSetor = usuario.getSetor().getIdSetor();
        List<Usuario> listaDeFuncionariosDoSetorDoGestor = usuarioRepository.fltrarPorSetorDoGestor(idSetor);
        List<ListaSetorResponseDto> listaFiltrada = listaDeFuncionariosDoSetorDoGestor.stream()
                .filter(lista -> lista.getIdUsuario() != id)
                .map(lista -> new ListaSetorResponseDto(
                        lista.getIdUsuario(),
                        lista.getNome(),
                        lista.getCargo().getCargo()
                ))
                .toList();
        return listaFiltrada;
    }
    public void indicar(long idFuncionario, String sub){
        long idGestor = Integer.parseInt(sub);
        List<ListaSetorResponseDto> listaDeUsuarios = listaDeFuncionariosDoSetorDoGestor(sub);
        long quantidadeDePessoasComCargoPRR = listaDeUsuarios.stream()
                .filter(lista -> lista.cargo().equals("RESPONSAVEL_PRR"))
                .count();
        if(quantidadeDePessoasComCargoPRR < 2){
            Usuario usuario = usuarioRepository.findById(idFuncionario).get();
            Cargo cargo = usuario.getCargo();
            cargo.setIdCargo(4);
            usuario.setCargo(cargo);
            usuarioRepository.save(usuario);
        }else {
            throw new QuantidadeException("Quantidade de pessoas com o cargo RESPONSAVEL_PRR não pode ser excedida");
        }
    }
}

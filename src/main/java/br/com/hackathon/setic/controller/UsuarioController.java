package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permite que o front-end acesse a API sem problemas de CORS
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Rota para listar todos os usuários cadastrados: GET http://localhost:8080/api/usuarios
    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // Rota para criar um novo usuário: POST http://localhost:8080/api/usuarios
    @PostMapping
    public Usuario criar(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
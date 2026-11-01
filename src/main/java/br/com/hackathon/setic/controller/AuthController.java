package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Valida se a senha confere (simples ou criptografada, dependendo do seu projeto)
            if (usuario.getSenha().equals(loginRequest.getSenha()) && usuario.isAtivo()) {
                return ResponseEntity.ok(usuario); // Login bem-sucedido
            }
        }
        return ResponseEntity.status(401).body("E-mail ou senha inválidos, ou usuário inativo.");
    }
}
package br.com.hackathon.setic.service;

import br.com.hackathon.setic.dto.LoginDto;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class LoginService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;
    public LoginService(UsuarioRepository usuarioRepository, PasswordEncoder encoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }
    public String login(LoginDto loginDto) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(loginDto.email());
        if(usuario.isPresent() && usuario.get().isAtivo()){
            if(encoder.matches(loginDto.senha(), usuario.get().getSenha())){
                return tokenService.gerarToken(usuario.get());
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "credenciais inválidas");
    }
}

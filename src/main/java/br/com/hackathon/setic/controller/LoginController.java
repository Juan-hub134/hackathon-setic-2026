package br.com.hackathon.setic.controller;

import br.com.hackathon.setic.dto.LoginDto;
import br.com.hackathon.setic.model.Usuario;
import br.com.hackathon.setic.repository.UsuarioRepository;
import br.com.hackathon.setic.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
public class LoginController {
    private final LoginService loginService;
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto login) {
        try {
            return ResponseEntity.ok(loginService.login(login));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
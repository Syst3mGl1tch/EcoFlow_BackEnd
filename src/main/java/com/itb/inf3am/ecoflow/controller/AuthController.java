package com.itb.inf3am.ecoflow.controller;

import com.itb.inf3am.ecoflow.dto.LoginRequestDTO;
import com.itb.inf3am.ecoflow.repository.UsuarioRepository;
import com.itb.inf3am.ecoflow.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        if (dto.username() == null || dto.username().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username obrigatorio");
        }
        if (dto.password() == null || dto.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha obrigatoria");
        }

        return usuarioRepository.findByUsername(dto.username().trim())
                .filter(u -> "ATIVO".equals(u.getStatusUsuario()))
                .filter(u -> passwordEncoder.matches(dto.password(), u.getPassword()))
                .map(usuarioService::toDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha invalidos"));
    }
}

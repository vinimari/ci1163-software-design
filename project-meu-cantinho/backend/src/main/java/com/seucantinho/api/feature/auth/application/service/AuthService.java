package com.seucantinho.api.feature.auth.application.service;

import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.auth.application.dto.LoginRequest;
import com.seucantinho.api.feature.auth.application.dto.LoginResponse;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.feature.usuario.infrastructure.persistence.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new BusinessException("Email ou senha inválidos");
        }

        String token = "Bearer " + usuario.getEmail();

        return new LoginResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getPerfil(),
            token
        );
    }
}

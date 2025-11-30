package com.seucantinho.api.feature.auth.application.service;

import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.auth.application.dto.LoginRequest;
import com.seucantinho.api.feature.auth.application.dto.LoginResponse;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.feature.usuario.domain.port.out.UsuarioRepositoryPort;
import com.seucantinho.api.feature.auth.domain.port.out.PasswordEncoderPort;
import com.seucantinho.api.feature.auth.domain.port.in.AuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServicePort {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepositoryPort.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        if (!passwordEncoderPort.matches(request.senha(), usuario.getSenhaHash())) {
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

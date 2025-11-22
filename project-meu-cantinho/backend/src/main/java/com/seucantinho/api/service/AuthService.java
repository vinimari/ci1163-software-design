package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Usuario;
import com.seucantinho.api.dto.auth.LoginRequest;
import com.seucantinho.api.dto.auth.LoginResponse;
import com.seucantinho.api.exception.BusinessException;
import com.seucantinho.api.repository.UsuarioRepository;
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

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new BusinessException("Email ou senha inválidos");
        }

        // Por enquanto, retorna um token simples (em produção, use JWT)
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

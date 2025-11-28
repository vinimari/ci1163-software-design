package com.seucantinho.api.feature.auth.application.dto;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;

public record LoginResponse(
    Integer id,
    String nome,
    String email,
    PerfilUsuarioEnum perfil,
    String token
) {}

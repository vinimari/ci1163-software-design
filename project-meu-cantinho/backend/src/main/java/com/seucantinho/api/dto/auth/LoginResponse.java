package com.seucantinho.api.dto.auth;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;

public record LoginResponse(
    Long id,
    String nome,
    String email,
    PerfilUsuarioEnum perfil,
    String token
) {}

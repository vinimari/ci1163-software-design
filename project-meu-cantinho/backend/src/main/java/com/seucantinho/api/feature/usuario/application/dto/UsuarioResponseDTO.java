package com.seucantinho.api.feature.usuario.application.dto;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UsuarioResponseDTO {

    private Integer id;
    private String nome;
    private String email;
    private PerfilUsuarioEnum perfil;
    private String cpf;
    private String telefone;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
}

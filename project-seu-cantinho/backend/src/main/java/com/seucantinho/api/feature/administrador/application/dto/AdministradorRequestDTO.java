package com.seucantinho.api.feature.administrador.application.dto;

import com.seucantinho.api.feature.usuario.application.dto.UsuarioRequestDTO;


import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AdministradorRequestDTO extends UsuarioRequestDTO {

    @Builder
    public AdministradorRequestDTO(String nome, String email, String senha,
                                   String cpf, String telefone, Boolean ativo) {
        super(nome, email, senha, cpf, telefone, ativo);
    }

    @Override
    public PerfilUsuarioEnum getPerfil() {
        return PerfilUsuarioEnum.ADMIN;
    }
}

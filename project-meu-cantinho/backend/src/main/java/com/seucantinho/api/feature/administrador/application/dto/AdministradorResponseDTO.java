package com.seucantinho.api.feature.administrador.application.dto;

import com.seucantinho.api.feature.usuario.application.dto.UsuarioResponseDTO;
import com.seucantinho.api.feature.usuario.domain.Usuario;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AdministradorResponseDTO extends UsuarioResponseDTO {

    @Builder
    public AdministradorResponseDTO(Integer id, String nome, String email,
                                    String cpf, String telefone, Boolean ativo,
                                    LocalDateTime dataCadastro) {
        super(id, nome, email, PerfilUsuarioEnum.ADMIN, cpf, telefone, ativo, dataCadastro);
    }
}

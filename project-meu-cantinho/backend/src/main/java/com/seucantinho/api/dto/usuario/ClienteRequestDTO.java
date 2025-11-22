package com.seucantinho.api.dto.usuario;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ClienteRequestDTO extends UsuarioRequestDTO {

    @Builder
    public ClienteRequestDTO(String nome, String email, String senha,
                            String cpf, String telefone, Boolean ativo) {
        super(nome, email, senha, cpf, telefone, ativo);
    }

    @Override
    public PerfilUsuarioEnum getPerfil() {
        return PerfilUsuarioEnum.CLIENTE;
    }
}

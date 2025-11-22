package com.seucantinho.api.dto.usuario;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ClienteResponseDTO extends UsuarioResponseDTO {

    private Integer quantidadeReservas;

    @Builder
    public ClienteResponseDTO(Integer id, String nome, String email,
                             String cpf, String telefone, Boolean ativo,
                             LocalDateTime dataCadastro, Integer quantidadeReservas) {
        super(id, nome, email, PerfilUsuarioEnum.CLIENTE, cpf, telefone, ativo, dataCadastro);
        this.quantidadeReservas = quantidadeReservas;
    }
}

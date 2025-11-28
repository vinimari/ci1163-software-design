package com.seucantinho.api.feature.cliente.application.dto;

import com.seucantinho.api.feature.usuario.application.dto.UsuarioResponseDTO;
import com.seucantinho.api.feature.usuario.domain.Usuario;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
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

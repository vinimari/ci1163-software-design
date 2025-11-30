package com.seucantinho.api.feature.funcionario.application.dto;

import com.seucantinho.api.feature.usuario.application.dto.UsuarioResponseDTO;


import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Dados de resposta de um funcionário com informações da filial")
public class FuncionarioResponseDTO extends UsuarioResponseDTO {

    @Schema(description = "Matrícula única do funcionário", example = "F001")
    private String matricula;

    @Schema(description = "Dados completos da filial onde o funcionário está alocado")
    private FilialResponseDTO filial;

    @Builder
    public FuncionarioResponseDTO(Integer id, String nome, String email,
                                 String cpf, String telefone, Boolean ativo,
                                 LocalDateTime dataCadastro, String matricula,
                                 FilialResponseDTO filial) {
        super(id, nome, email, PerfilUsuarioEnum.FUNCIONARIO, cpf, telefone, ativo, dataCadastro);
        this.matricula = matricula;
        this.filial = filial;
    }
}

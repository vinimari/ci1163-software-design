package com.seucantinho.api.feature.funcionario.application.dto;

import com.seucantinho.api.feature.usuario.application.dto.UsuarioRequestDTO;
import com.seucantinho.api.feature.usuario.domain.Usuario;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class FuncionarioRequestDTO extends UsuarioRequestDTO {

    @NotBlank(message = "Matrícula é obrigatória")
    @Size(max = 50, message = "Matrícula deve ter no máximo 50 caracteres")
    private String matricula;

    @NotNull(message = "ID da filial é obrigatório")
    private Integer filialId;

    @Builder
    public FuncionarioRequestDTO(String nome, String email, String senha,
                                String cpf, String telefone, Boolean ativo,
                                String matricula, Integer filialId) {
        super(nome, email, senha, cpf, telefone, ativo);
        this.matricula = matricula;
        this.filialId = filialId;
    }

    @Override
    public PerfilUsuarioEnum getPerfil() {
        return PerfilUsuarioEnum.FUNCIONARIO;
    }
}

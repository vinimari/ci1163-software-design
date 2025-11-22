package com.seucantinho.api.dto.usuario;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FuncionarioResponseDTO extends UsuarioResponseDTO {

    private String matricula;
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

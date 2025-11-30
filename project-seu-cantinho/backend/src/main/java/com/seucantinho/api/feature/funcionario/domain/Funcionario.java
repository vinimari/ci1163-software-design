package com.seucantinho.api.feature.funcionario.domain;

import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.usuario.domain.Usuario;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("FUNCIONARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario extends Usuario {

    @Column(unique = true, length = 50)
    private String matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filial_id")
    private Filial filial;

    @Override
    public PerfilUsuarioEnum getPerfil() {
        return PerfilUsuarioEnum.FUNCIONARIO;
    }
}

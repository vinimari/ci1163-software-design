package com.seucantinho.api.feature.administrador.domain;

import com.seucantinho.api.feature.usuario.domain.Usuario;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {

    @Override
    public PerfilUsuarioEnum getPerfil() {
        return PerfilUsuarioEnum.ADMIN;
    }
}

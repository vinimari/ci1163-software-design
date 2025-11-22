package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
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

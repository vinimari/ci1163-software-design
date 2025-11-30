package com.seucantinho.api.feature.cliente.domain;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.usuario.domain.Usuario;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cliente extends Usuario {

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Reserva> reservas = new ArrayList<>();

    @Override
    public PerfilUsuarioEnum getPerfil() {
        return PerfilUsuarioEnum.CLIENTE;
    }
}

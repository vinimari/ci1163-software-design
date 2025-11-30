package com.seucantinho.api.feature.reserva.infrastructure.mapper;

import com.seucantinho.api.feature.espaco.infrastructure.mapper.EspacoMapper;
import com.seucantinho.api.feature.usuario.infrastructure.mapper.UsuarioMapper;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import com.seucantinho.api.feature.reserva.application.dto.ReservaRequestDTO;
import com.seucantinho.api.feature.reserva.application.dto.ReservaResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservaMapper {

    private final EspacoMapper espacoMapper;
    private final UsuarioMapper usuarioMapper;

    public Reserva toEntity(ReservaRequestDTO dto, Usuario usuario, Espaco espaco) {
        return Reserva.builder()
                .dataEvento(DataEvento.of(dto.getDataEvento()))
                .valorTotal(ValorMonetario.of(dto.getValorTotal()))
                .observacoes(dto.getObservacoes())
                .status(dto.getStatus() != null ? dto.getStatus() : StatusReservaEnum.AGUARDANDO_SINAL)
                .usuario(usuario)
                .espaco(espaco)
                .build();
    }

    public ReservaResponseDTO toResponseDTO(Reserva reserva) {
        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .dataCriacao(reserva.getDataCriacao())
                .dataEvento(reserva.getDataEvento().getData())
                .valorTotal(reserva.getValorTotal().getValor())
                .observacoes(reserva.getObservacoes())
                .status(reserva.getStatus())
                .totalPago(reserva.calcularTotalPago().getValor())
                .saldo(reserva.calcularSaldo().getValor())
                .usuario(usuarioMapper.toResponseDTO(reserva.getUsuario()))
                .espaco(espacoMapper.toResponseDTO(reserva.getEspaco()))
                .build();
    }
}

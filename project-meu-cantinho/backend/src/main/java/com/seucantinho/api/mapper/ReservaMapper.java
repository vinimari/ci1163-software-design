package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Cliente;
import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.entity.Usuario;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.dto.reserva.ReservaRequestDTO;
import com.seucantinho.api.dto.reserva.ReservaResponseDTO;
import com.seucantinho.api.dto.usuario.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservaMapper {

    private final EspacoMapper espacoMapper;
    private final UsuarioMapper usuarioMapper;

    public Reserva toEntity(ReservaRequestDTO dto, Usuario usuario, Espaco espaco) {
        return Reserva.builder()
                .dataEvento(dto.getDataEvento())
                .valorTotal(dto.getValorTotal())
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
                .dataEvento(reserva.getDataEvento())
                .valorTotal(reserva.getValorTotal())
                .observacoes(reserva.getObservacoes())
                .status(reserva.getStatus())
                .totalPago(reserva.calcularTotalPago())
                .saldo(reserva.calcularSaldo())
                .usuario(usuarioMapper.toResponseDTO(reserva.getUsuario()))
                .espaco(espacoMapper.toResponseDTO(reserva.getEspaco()))
                .build();
    }
}

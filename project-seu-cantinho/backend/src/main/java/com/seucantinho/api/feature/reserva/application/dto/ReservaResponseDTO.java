package com.seucantinho.api.feature.reserva.application.dto;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.espaco.application.dto.EspacoResponseDTO;
import com.seucantinho.api.feature.usuario.application.dto.UsuarioResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {

    private Integer id;
    private LocalDateTime dataCriacao;
    private LocalDate dataEvento;
    private BigDecimal valorTotal;
    private String observacoes;
    private StatusReservaEnum status;
    private UsuarioResponseDTO usuario;
    private EspacoResponseDTO espaco;
    private BigDecimal totalPago;
    private BigDecimal saldo;
}

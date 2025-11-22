package com.seucantinho.api.dto.reserva;

import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;
import com.seucantinho.api.dto.usuario.UsuarioResponseDTO;
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

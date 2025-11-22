package com.seucantinho.api.dto.reserva;

import com.seucantinho.api.domain.enums.StatusReservaEnum;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRequestDTO {

    @NotNull(message = "Data do evento é obrigatória")
    @Future(message = "Data do evento deve ser futura")
    private LocalDate dataEvento;

    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor total deve ser maior que zero")
    private BigDecimal valorTotal;

    private String observacoes;

    private StatusReservaEnum status;

    @NotNull(message = "ID do usuário é obrigatório")
    private Integer usuarioId;

    @NotNull(message = "ID do espaço é obrigatório")
    private Integer espacoId;
}

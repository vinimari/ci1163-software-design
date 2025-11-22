package com.seucantinho.api.dto.pagamento;

import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoRequestDTO {

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Tipo de pagamento é obrigatório")
    private TipoPagamentoEnum tipo;

    @Size(max = 50, message = "Forma de pagamento deve ter no máximo 50 caracteres")
    private String formaPagamento;

    @Size(max = 100, message = "Código de transação deve ter no máximo 100 caracteres")
    private String codigoTransacaoGateway;

    @NotNull(message = "ID da reserva é obrigatório")
    private Integer reservaId;
}

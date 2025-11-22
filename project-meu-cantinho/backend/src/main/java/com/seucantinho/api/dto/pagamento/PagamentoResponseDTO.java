package com.seucantinho.api.dto.pagamento;

import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoResponseDTO {

    private Integer id;
    private LocalDateTime dataPagamento;
    private BigDecimal valor;
    private TipoPagamentoEnum tipo;
    private String formaPagamento;
    private String codigoTransacaoGateway;
    private Integer reservaId;
}

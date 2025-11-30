package com.seucantinho.api.feature.pagamento.application.dto;

import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
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

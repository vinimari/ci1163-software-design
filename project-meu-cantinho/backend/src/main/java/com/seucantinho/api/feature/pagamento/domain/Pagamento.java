package com.seucantinho.api.feature.pagamento.domain;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_pagamento", updatable = false)
    private LocalDateTime dataPagamento;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(nullable = false, precision = 10, scale = 2))
    private ValorMonetario valor;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "tipo_pagamento_enum")
    private TipoPagamentoEnum tipo;

    @Column(name = "forma_pagamento", length = 50)
    private String formaPagamento;

    @Column(name = "codigo_transacao_gateway", length = 100)
    private String codigoTransacaoGateway;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @PrePersist
    protected void onCreate() {
        dataPagamento = LocalDateTime.now();
    }
}

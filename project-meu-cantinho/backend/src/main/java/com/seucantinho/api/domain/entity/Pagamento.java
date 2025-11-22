package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
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

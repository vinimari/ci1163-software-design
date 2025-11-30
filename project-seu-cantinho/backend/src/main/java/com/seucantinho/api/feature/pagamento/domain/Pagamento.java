package com.seucantinho.api.feature.pagamento.domain;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


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

    public void validar() {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva é obrigatória");
        }
        if (valor == null) {
            throw new IllegalArgumentException("Valor é obrigatório");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de pagamento é obrigatório");
        }

        if (valor.getValor().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        validarTipoPagamento();
    }

    private void validarTipoPagamento() {
        ValorMonetario valorTotal = reserva.getValorTotal();
        ValorMonetario metadeValor = valorTotal.calcularMetade();
        boolean possuiPagamentos = !reserva.getPagamentos().isEmpty();

        switch (tipo) {
            case TOTAL:
                if (possuiPagamentos) {
                    throw new IllegalArgumentException("Pagamento TOTAL só pode ser feito na criação da reserva");
                }
                if (!valor.isIgualA(valorTotal)) {
                    throw new IllegalArgumentException(
                        String.format("Pagamento TOTAL deve ser o valor completo: %s", valorTotal.getValorFormatado())
                    );
                }
                break;

            case SINAL:
                if (possuiPagamentos) {
                    throw new IllegalArgumentException("Pagamento SINAL só pode ser feito na criação da reserva");
                }
                if (!valor.isIgualA(metadeValor)) {
                    throw new IllegalArgumentException(
                        String.format("Pagamento SINAL deve ser 50%% do valor total: %s", metadeValor.getValorFormatado())
                    );
                }
                break;

            case QUITACAO:
                if (!possuiPagamentos) {
                    throw new IllegalArgumentException("Pagamento QUITACAO só pode ser feito após o pagamento do SINAL");
                }
                if (reserva.getPagamentos().get(0).getTipo() != TipoPagamentoEnum.SINAL) {
                    throw new IllegalArgumentException("Pagamento QUITACAO só é permitido para reservas com pagamento inicial do tipo SINAL");
                }
                if (reserva.getPagamentos().size() > 1) {
                    throw new IllegalArgumentException("Esta reserva já foi quitada");
                }
                ValorMonetario saldoRestante = reserva.calcularSaldo();
                if (!valor.isIgualA(saldoRestante)) {
                    throw new IllegalArgumentException(
                        String.format("Pagamento QUITACAO deve ser o saldo restante: %s", saldoRestante.getValorFormatado())
                    );
                }
                break;

            default:
                throw new IllegalArgumentException("Tipo de pagamento inválido");
        }
    }
}

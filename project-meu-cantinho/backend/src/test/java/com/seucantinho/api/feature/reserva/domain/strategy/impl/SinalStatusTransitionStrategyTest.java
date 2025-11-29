package com.seucantinho.api.feature.reserva.domain.strategy.impl;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do SinalStatusTransitionStrategy")
class SinalStatusTransitionStrategyTest {

    private SinalStatusTransitionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SinalStatusTransitionStrategy();
    }

    @Test
    @DisplayName("Deve determinar novo status como CONFIRMADA para pagamento de sinal")
    void deveDeterminarStatusConfirmada() {
        Pagamento pagamento = Pagamento.builder()
                .tipo(TipoPagamentoEnum.SINAL)
                .valor(ValorMonetario.of(new BigDecimal("100.00")))
                .build();

        StatusReservaEnum newStatus = strategy.determineNewStatus(pagamento);

        assertEquals(StatusReservaEnum.CONFIRMADA, newStatus);
    }

    @Test
    @DisplayName("Deve retornar true para canHandle quando tipo é SINAL")
    void deveRetornarTrueParaSinal() {
        Pagamento pagamento = Pagamento.builder()
                .tipo(TipoPagamentoEnum.SINAL)
                .valor(ValorMonetario.of(new BigDecimal("100.00")))
                .build();

        assertTrue(strategy.canHandle(pagamento));
    }

    @Test
    @DisplayName("Deve retornar false para canHandle quando tipo não é SINAL")
    void deveRetornarFalseParaNaoSinal() {
        Pagamento pagamentoQuitacao = Pagamento.builder()
                .tipo(TipoPagamentoEnum.QUITACAO)
                .valor(ValorMonetario.of(new BigDecimal("100.00")))
                .build();

        Pagamento pagamentoTotal = Pagamento.builder()
                .tipo(TipoPagamentoEnum.TOTAL)
                .valor(ValorMonetario.of(new BigDecimal("100.00")))
                .build();

        assertFalse(strategy.canHandle(pagamentoQuitacao));
        assertFalse(strategy.canHandle(pagamentoTotal));
    }
}


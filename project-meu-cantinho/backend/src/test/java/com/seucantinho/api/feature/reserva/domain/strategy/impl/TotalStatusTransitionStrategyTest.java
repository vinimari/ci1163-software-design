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

@DisplayName("Testes do TotalStatusTransitionStrategy")
class TotalStatusTransitionStrategyTest {

    private TotalStatusTransitionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TotalStatusTransitionStrategy();
    }

    @Test
    @DisplayName("Deve determinar novo status como QUITADA para pagamento total")
    void deveDeterminarStatusQuitada() {
        Pagamento pagamento = Pagamento.builder()
                .tipo(TipoPagamentoEnum.TOTAL)
                .valor(ValorMonetario.of(new BigDecimal("1000.00")))
                .build();

        StatusReservaEnum newStatus = strategy.determineNewStatus(pagamento);

        assertEquals(StatusReservaEnum.QUITADA, newStatus);
    }

    @Test
    @DisplayName("Deve retornar true para canHandle quando tipo é TOTAL")
    void deveRetornarTrueParaTotal() {
        Pagamento pagamento = Pagamento.builder()
                .tipo(TipoPagamentoEnum.TOTAL)
                .valor(ValorMonetario.of(new BigDecimal("1000.00")))
                .build();

        assertTrue(strategy.canHandle(pagamento));
    }

    @Test
    @DisplayName("Deve retornar false para canHandle quando tipo não é TOTAL")
    void deveRetornarFalseParaNaoTotal() {
        Pagamento pagamentoSinal = Pagamento.builder()
                .tipo(TipoPagamentoEnum.SINAL)
                .valor(ValorMonetario.of(new BigDecimal("100.00")))
                .build();

        Pagamento pagamentoQuitacao = Pagamento.builder()
                .tipo(TipoPagamentoEnum.QUITACAO)
                .valor(ValorMonetario.of(new BigDecimal("500.00")))
                .build();

        assertFalse(strategy.canHandle(pagamentoSinal));
        assertFalse(strategy.canHandle(pagamentoQuitacao));
    }
}


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

@DisplayName("Testes do QuitacaoStatusTransitionStrategy")
class QuitacaoStatusTransitionStrategyTest {

    private QuitacaoStatusTransitionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new QuitacaoStatusTransitionStrategy();
    }

    @Test
    @DisplayName("Deve determinar novo status como QUITADA para pagamento de quitação")
    void deveDeterminarStatusQuitada() {
        Pagamento pagamento = Pagamento.builder()
                .tipo(TipoPagamentoEnum.QUITACAO)
                .valor(ValorMonetario.of(new BigDecimal("500.00")))
                .build();

        StatusReservaEnum newStatus = strategy.determineNewStatus(pagamento);

        assertEquals(StatusReservaEnum.QUITADA, newStatus);
    }

    @Test
    @DisplayName("Deve retornar true para canHandle quando tipo é QUITACAO")
    void deveRetornarTrueParaQuitacao() {
        Pagamento pagamento = Pagamento.builder()
                .tipo(TipoPagamentoEnum.QUITACAO)
                .valor(ValorMonetario.of(new BigDecimal("500.00")))
                .build();

        assertTrue(strategy.canHandle(pagamento));
    }

    @Test
    @DisplayName("Deve retornar false para canHandle quando tipo não é QUITACAO")
    void deveRetornarFalseParaNaoQuitacao() {
        Pagamento pagamentoSinal = Pagamento.builder()
                .tipo(TipoPagamentoEnum.SINAL)
                .valor(ValorMonetario.of(new BigDecimal("100.00")))
                .build();

        Pagamento pagamentoTotal = Pagamento.builder()
                .tipo(TipoPagamentoEnum.TOTAL)
                .valor(ValorMonetario.of(new BigDecimal("600.00")))
                .build();

        assertFalse(strategy.canHandle(pagamentoSinal));
        assertFalse(strategy.canHandle(pagamentoTotal));
    }
}


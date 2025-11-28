package com.seucantinho.api.validator;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PagamentoValidatorTest {

    @InjectMocks
    private PagamentoValidator pagamentoValidator;

    private Reserva reserva;

    @BeforeEach
    void setUp() {
        reserva = Reserva.builder()
                .id(1)
                .valorTotal(new BigDecimal("1000.00"))
                .pagamentos(new ArrayList<>())
                .build();
    }

    @Test
    void shouldValidateValorPagamento_WhenValid() {
        // Given
        // Given
        BigDecimal valorPagamento = new BigDecimal("500.00");
        // When & Then

        // When & Then
        assertThatCode(() -> pagamentoValidator.validateValorPagamento(valorPagamento, reserva))
                .doesNotThrowAnyException();
    }
        // Given

        // When & Then
    @Test
    void shouldValidateValorPagamento_WhenExactlyTotalValue() {
        // Given
        BigDecimal valorPagamento = new BigDecimal("1000.00");

        // Given
        // When & Then
        assertThatCode(() -> pagamentoValidator.validateValorPagamento(valorPagamento, reserva))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldValidateValorPagamento_WithExistingPagamentos() {
        // When & Then
        // Given
        Pagamento pagamentoExistente = Pagamento.builder()
                .id(1)
                .valor(new BigDecimal("600.00"))
                .build();
        // Given
        reserva.getPagamentos().add(pagamentoExistente);
        // When & Then

        BigDecimal novoPagamento = new BigDecimal("400.00");

        // When & Then
        assertThatCode(() -> pagamentoValidator.validateValorPagamento(novoPagamento, reserva))
                .doesNotThrowAnyException();
        // Given
    }

    @Test
    void shouldThrowException_WhenExceedsTotalValue() {
        // Given
        BigDecimal valorPagamento = new BigDecimal("1100.00");

        // When & Then
        assertThatThrownBy(() -> pagamentoValidator.validateValorPagamento(valorPagamento, reserva))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor do pagamento excede o saldo da reserva");
    }

        // When & Then
    @Test
    void shouldThrowException_WhenExceedsTotalWithExistingPagamentos() {
        // Given
        Pagamento pagamento1 = Pagamento.builder()
                .id(1)
                .valor(new BigDecimal("600.00"))
        // Given - Primeiro pagamento
                .build();

        Pagamento pagamento2 = Pagamento.builder()
                .id(2)
                .valor(new BigDecimal("300.00"))
        // Given - Segundo pagamento
                .build();

        reserva.setPagamentos(Arrays.asList(pagamento1, pagamento2));

        BigDecimal novoPagamento = new BigDecimal("200.00");
        // Given - Terceiro pagamento

        // When & Then
        assertThatThrownBy(() -> pagamentoValidator.validateValorPagamento(novoPagamento, reserva))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor do pagamento excede o saldo da reserva");
        // Given - Quarto pagamento (completa o valor)
    }

    @Test
    void shouldValidateValorPagamento_WhenMultipleSmallPayments() {
        // Given - Primeiro pagamento
        BigDecimal pagamento1 = new BigDecimal("250.00");
        // Given
        assertThatCode(() -> pagamentoValidator.validateValorPagamento(pagamento1, reserva))
        // When & Then
                .doesNotThrowAnyException();

        reserva.getPagamentos().add(Pagamento.builder().valor(pagamento1).build());

        // Given - Segundo pagamento
        // Given
        BigDecimal pagamento2 = new BigDecimal("250.00");
        assertThatCode(() -> pagamentoValidator.validateValorPagamento(pagamento2, reserva))
                .doesNotThrowAnyException();

        reserva.getPagamentos().add(Pagamento.builder().valor(pagamento2).build());

        // Given - Terceiro pagamento
        // When & Then
        BigDecimal pagamento3 = new BigDecimal("250.00");
        assertThatCode(() -> pagamentoValidator.validateValorPagamento(pagamento3, reserva))
                .doesNotThrowAnyException();

        reserva.getPagamentos().add(Pagamento.builder().valor(pagamento3).build());

        // Given - Quarto pagamento (completa o valor)
        BigDecimal pagamento4 = new BigDecimal("250.00");
        assertThatCode(() -> pagamentoValidator.validateValorPagamento(pagamento4, reserva))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldValidateZeroValue() {
        // Given
        BigDecimal valorPagamento = BigDecimal.ZERO;

        // When & Then
        assertThatCode(() -> pagamentoValidator.validateValorPagamento(valorPagamento, reserva))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_WhenSmallExcess() {
        // Given
        Pagamento pagamentoExistente = Pagamento.builder()
                .id(1)
                .valor(new BigDecimal("999.99"))
                .build();
        reserva.getPagamentos().add(pagamentoExistente);

        BigDecimal novoPagamento = new BigDecimal("0.02");

        // When & Then
        assertThatThrownBy(() -> pagamentoValidator.validateValorPagamento(novoPagamento, reserva))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor do pagamento excede o saldo da reserva");
    }
}

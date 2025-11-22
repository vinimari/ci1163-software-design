package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoTest {

    @Test
    void shouldCreatePagamentoWithBuilder() {
        Reserva reserva = new Reserva();
        LocalDateTime now = LocalDateTime.now();

        Pagamento pagamento = Pagamento.builder()
            .id(1)
            .dataPagamento(now)
            .valor(new BigDecimal("500.00"))
            .tipo(TipoPagamentoEnum.SINAL)
            .formaPagamento("Cartão de Crédito")
            .codigoTransacaoGateway("TRX123456")
            .reserva(reserva)
            .build();

        assertNotNull(pagamento);
        assertEquals(1, pagamento.getId());
        assertEquals(now, pagamento.getDataPagamento());
        assertEquals(0, new BigDecimal("500.00").compareTo(pagamento.getValor()));
        assertEquals(TipoPagamentoEnum.SINAL, pagamento.getTipo());
        assertEquals("Cartão de Crédito", pagamento.getFormaPagamento());
        assertEquals("TRX123456", pagamento.getCodigoTransacaoGateway());
        assertEquals(reserva, pagamento.getReserva());
    }

    @Test
    void shouldSetAndGetAllProperties() {
        Pagamento pagamento = new Pagamento();
        Reserva reserva = new Reserva();
        LocalDateTime now = LocalDateTime.now();

        pagamento.setId(10);
        pagamento.setDataPagamento(now);
        pagamento.setValor(new BigDecimal("750.50"));
        pagamento.setTipo(TipoPagamentoEnum.QUITACAO);
        pagamento.setFormaPagamento("PIX");
        pagamento.setCodigoTransacaoGateway("PIX987654");
        pagamento.setReserva(reserva);

        assertEquals(10, pagamento.getId());
        assertEquals(now, pagamento.getDataPagamento());
        assertEquals(0, new BigDecimal("750.50").compareTo(pagamento.getValor()));
        assertEquals(TipoPagamentoEnum.QUITACAO, pagamento.getTipo());
        assertEquals("PIX", pagamento.getFormaPagamento());
        assertEquals("PIX987654", pagamento.getCodigoTransacaoGateway());
        assertEquals(reserva, pagamento.getReserva());
    }

    @Test
    void shouldCreatePagamentoWithSinalType() {
        Pagamento pagamento = Pagamento.builder()
            .tipo(TipoPagamentoEnum.SINAL)
            .valor(new BigDecimal("300.00"))
            .build();

        assertEquals(TipoPagamentoEnum.SINAL, pagamento.getTipo());
    }

    @Test
    void shouldCreatePagamentoWithQuitacaoType() {
        Pagamento pagamento = Pagamento.builder()
            .tipo(TipoPagamentoEnum.QUITACAO)
            .valor(new BigDecimal("700.00"))
            .build();

        assertEquals(TipoPagamentoEnum.QUITACAO, pagamento.getTipo());
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        Pagamento pagamento = new Pagamento();
        assertNotNull(pagamento);
        assertNull(pagamento.getId());
        assertNull(pagamento.getValor());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        Reserva reserva = new Reserva();
        LocalDateTime now = LocalDateTime.now();

        Pagamento pagamento = new Pagamento(
            1,
            now,
            new BigDecimal("450.00"),
            TipoPagamentoEnum.SINAL,
            "Débito",
            "DEB123",
            reserva
        );

        assertEquals(1, pagamento.getId());
        assertEquals(now, pagamento.getDataPagamento());
        assertEquals(0, new BigDecimal("450.00").compareTo(pagamento.getValor()));
        assertEquals(TipoPagamentoEnum.SINAL, pagamento.getTipo());
        assertEquals("Débito", pagamento.getFormaPagamento());
        assertEquals("DEB123", pagamento.getCodigoTransacaoGateway());
        assertEquals(reserva, pagamento.getReserva());
    }

    @Test
    void shouldAllowNullFormaPagamento() {
        Pagamento pagamento = new Pagamento();
        pagamento.setFormaPagamento(null);
        assertNull(pagamento.getFormaPagamento());
    }

    @Test
    void shouldAllowNullCodigoTransacao() {
        Pagamento pagamento = new Pagamento();
        pagamento.setCodigoTransacaoGateway(null);
        assertNull(pagamento.getCodigoTransacaoGateway());
    }

    @Test
    void shouldHandleDifferentValorPrecisions() {
        Pagamento pagamento1 = Pagamento.builder()
            .valor(new BigDecimal("100.00"))
            .build();

        Pagamento pagamento2 = Pagamento.builder()
            .valor(new BigDecimal("100.50"))
            .build();

        Pagamento pagamento3 = Pagamento.builder()
            .valor(new BigDecimal("99.99"))
            .build();

        assertEquals(0, new BigDecimal("100.00").compareTo(pagamento1.getValor()));
        assertEquals(0, new BigDecimal("100.50").compareTo(pagamento2.getValor()));
        assertEquals(0, new BigDecimal("99.99").compareTo(pagamento3.getValor()));
    }

    @Test
    void shouldSetDataPagamentoOnCreate() throws Exception {
        Pagamento pagamento = new Pagamento();
        assertNull(pagamento.getDataPagamento());

        // Invocar o método onCreate via reflexão
        var method = Pagamento.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(pagamento);

        assertNotNull(pagamento.getDataPagamento());
    }
}

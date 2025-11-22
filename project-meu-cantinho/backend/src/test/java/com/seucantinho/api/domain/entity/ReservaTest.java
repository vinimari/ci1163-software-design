package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    @Test
    void shouldCreateReservaWithBuilder() {
        Usuario usuario = new Cliente();
        Espaco espaco = new Espaco();
        LocalDateTime now = LocalDateTime.now();
        LocalDate dataEvento = LocalDate.of(2025, 12, 31);

        Reserva reserva = Reserva.builder()
            .id(1)
            .dataCriacao(now)
            .dataEvento(dataEvento)
            .valorTotal(new BigDecimal("1000.00"))
            .observacoes("Festa de aniversário")
            .status(StatusReservaEnum.CONFIRMADA)
            .usuario(usuario)
            .espaco(espaco)
            .pagamentos(new ArrayList<>())
            .build();

        assertNotNull(reserva);
        assertEquals(1, reserva.getId());
        assertEquals(now, reserva.getDataCriacao());
        assertEquals(dataEvento, reserva.getDataEvento());
        assertEquals(0, new BigDecimal("1000.00").compareTo(reserva.getValorTotal()));
        assertEquals("Festa de aniversário", reserva.getObservacoes());
        assertEquals(StatusReservaEnum.CONFIRMADA, reserva.getStatus());
        assertEquals(usuario, reserva.getUsuario());
        assertEquals(espaco, reserva.getEspaco());
    }

    @Test
    void shouldSetAndGetAllProperties() {
        Reserva reserva = new Reserva();
        Usuario usuario = new Cliente();
        Espaco espaco = new Espaco();
        LocalDateTime now = LocalDateTime.now();
        LocalDate dataEvento = LocalDate.of(2025, 6, 15);

        reserva.setId(10);
        reserva.setDataCriacao(now);
        reserva.setDataEvento(dataEvento);
        reserva.setValorTotal(new BigDecimal("750.00"));
        reserva.setObservacoes("Reunião corporativa");
        reserva.setStatus(StatusReservaEnum.AGUARDANDO_SINAL);
        reserva.setUsuario(usuario);
        reserva.setEspaco(espaco);

        assertEquals(10, reserva.getId());
        assertEquals(now, reserva.getDataCriacao());
        assertEquals(dataEvento, reserva.getDataEvento());
        assertEquals(0, new BigDecimal("750.00").compareTo(reserva.getValorTotal()));
        assertEquals("Reunião corporativa", reserva.getObservacoes());
        assertEquals(StatusReservaEnum.AGUARDANDO_SINAL, reserva.getStatus());
        assertEquals(usuario, reserva.getUsuario());
        assertEquals(espaco, reserva.getEspaco());
    }

    @Test
    void shouldInitializePagamentosListByDefault() {
        Reserva reserva = Reserva.builder().build();
        assertNotNull(reserva.getPagamentos());
        assertTrue(reserva.getPagamentos().isEmpty());
    }

    @Test
    void shouldInitializeStatusAsAguardandoSinalByDefault() {
        Reserva reserva = Reserva.builder().build();
        assertEquals(StatusReservaEnum.AGUARDANDO_SINAL, reserva.getStatus());
    }

    @Test
    void shouldCalcularTotalPagoWithNoPagamentos() {
        Reserva reserva = Reserva.builder()
            .valorTotal(new BigDecimal("1000.00"))
            .pagamentos(new ArrayList<>())
            .build();

        BigDecimal totalPago = reserva.calcularTotalPago();
        assertEquals(0, BigDecimal.ZERO.compareTo(totalPago));
    }

    @Test
    void shouldCalcularTotalPagoWithSinglePagamento() {
        Reserva reserva = Reserva.builder()
            .valorTotal(new BigDecimal("1000.00"))
            .pagamentos(new ArrayList<>())
            .build();

        Pagamento pagamento = Pagamento.builder()
            .valor(new BigDecimal("300.00"))
            .tipo(TipoPagamentoEnum.SINAL)
            .reserva(reserva)
            .build();

        reserva.getPagamentos().add(pagamento);

        BigDecimal totalPago = reserva.calcularTotalPago();
        assertEquals(0, new BigDecimal("300.00").compareTo(totalPago));
    }

    @Test
    void shouldCalcularTotalPagoWithMultiplePagamentos() {
        Reserva reserva = Reserva.builder()
            .valorTotal(new BigDecimal("1000.00"))
            .pagamentos(new ArrayList<>())
            .build();

        Pagamento pagamento1 = Pagamento.builder()
            .valor(new BigDecimal("300.00"))
            .tipo(TipoPagamentoEnum.SINAL)
            .build();

        Pagamento pagamento2 = Pagamento.builder()
            .valor(new BigDecimal("700.00"))
            .tipo(TipoPagamentoEnum.SINAL)
            .build();

        reserva.getPagamentos().add(pagamento1);
        reserva.getPagamentos().add(pagamento2);

        BigDecimal totalPago = reserva.calcularTotalPago();
        assertEquals(0, new BigDecimal("1000.00").compareTo(totalPago));
    }

    @Test
    void shouldCalcularSaldoWithNoPagamentos() {
        Reserva reserva = Reserva.builder()
            .valorTotal(new BigDecimal("1000.00"))
            .pagamentos(new ArrayList<>())
            .build();

        BigDecimal saldo = reserva.calcularSaldo();
        assertEquals(0, new BigDecimal("1000.00").compareTo(saldo));
    }

    @Test
    void shouldCalcularSaldoWithPartialPagamento() {
        Reserva reserva = Reserva.builder()
            .valorTotal(new BigDecimal("1000.00"))
            .pagamentos(new ArrayList<>())
            .build();

        Pagamento pagamento = Pagamento.builder()
            .valor(new BigDecimal("400.00"))
            .tipo(TipoPagamentoEnum.SINAL)
            .build();

        reserva.getPagamentos().add(pagamento);

        BigDecimal saldo = reserva.calcularSaldo();
        assertEquals(0, new BigDecimal("600.00").compareTo(saldo));
    }

    @Test
    void shouldCalcularSaldoAsZeroWhenFullyPaid() {
        Reserva reserva = Reserva.builder()
            .valorTotal(new BigDecimal("1000.00"))
            .pagamentos(new ArrayList<>())
            .build();

        Pagamento pagamento1 = Pagamento.builder()
            .valor(new BigDecimal("400.00"))
            .tipo(TipoPagamentoEnum.SINAL)
            .build();

        Pagamento pagamento2 = Pagamento.builder()
            .valor(new BigDecimal("600.00"))
            .tipo(TipoPagamentoEnum.SINAL)
            .build();

        reserva.getPagamentos().add(pagamento1);
        reserva.getPagamentos().add(pagamento2);

        BigDecimal saldo = reserva.calcularSaldo();
        assertEquals(0, BigDecimal.ZERO.compareTo(saldo));
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        Reserva reserva = new Reserva();
        assertNotNull(reserva);
        assertNull(reserva.getId());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        Usuario usuario = new Cliente();
        Espaco espaco = new Espaco();
        LocalDateTime now = LocalDateTime.now();
        LocalDate dataEvento = LocalDate.of(2025, 7, 20);

        Reserva reserva = new Reserva(
            1,
            now,
            dataEvento,
            new BigDecimal("500.00"),
            "Observação",
            StatusReservaEnum.CANCELADA,
            usuario,
            espaco,
            new ArrayList<>()
        );

        assertEquals(1, reserva.getId());
        assertEquals(dataEvento, reserva.getDataEvento());
        assertEquals(StatusReservaEnum.CANCELADA, reserva.getStatus());
        assertNotNull(reserva.getPagamentos());
    }

    @Test
    void shouldAllowNullObservacoes() {
        Reserva reserva = new Reserva();
        reserva.setObservacoes(null);
        assertNull(reserva.getObservacoes());
    }
}

package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Cliente;
import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.dto.reserva.ReservaRequestDTO;
import com.seucantinho.api.dto.reserva.ReservaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaMapperTest {

    private ReservaMapper reservaMapper;
    private ReservaRequestDTO requestDTO;
    private Reserva reserva;
    private Cliente cliente;
    private Espaco espaco;

    @BeforeEach
    void setUp() {
        reservaMapper = new ReservaMapper();

        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .build();

        espaco = Espaco.builder()
                .id(1)
                .nome("Salão Principal")
                .precoDiaria(new BigDecimal("500.00"))
                .build();

        requestDTO = new ReservaRequestDTO(
                LocalDate.now().plusDays(30),
                new BigDecimal("500.00"),
                "Observação teste",
                StatusReservaEnum.AGUARDANDO_SINAL,
                1,
                1
        );

        reserva = Reserva.builder()
                .id(1)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(LocalDate.now().plusDays(30))
                .valorTotal(new BigDecimal("500.00"))
                .observacoes("Observação teste")
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .usuario(cliente)
                .espaco(espaco)
                .pagamentos(new ArrayList<>())
                .build();
    }

    @Test
    void shouldConvertRequestDTOToEntity() {
        // When
        Reserva result = reservaMapper.toEntity(requestDTO, cliente, espaco);

        // Then
        assertNotNull(result);
        assertEquals(LocalDate.now().plusDays(30), result.getDataEvento());
        assertEquals(new BigDecimal("500.00"), result.getValorTotal());
        assertEquals("Observação teste", result.getObservacoes());
        assertEquals(StatusReservaEnum.AGUARDANDO_SINAL, result.getStatus());
        assertEquals(cliente, result.getUsuario());
        assertEquals(espaco, result.getEspaco());
    }

    @Test
    void shouldHandleNullObservacoes() {
        // Given
        requestDTO.setObservacoes(null);

        // When
        Reserva result = reservaMapper.toEntity(requestDTO, cliente, espaco);

        // Then
        assertNull(result.getObservacoes());
    }

    @Test
    void shouldHandleNullStatus() {
        // Given
        requestDTO.setStatus(null);

        // When
        Reserva result = reservaMapper.toEntity(requestDTO, cliente, espaco);

        // Then
        assertEquals(StatusReservaEnum.AGUARDANDO_SINAL, result.getStatus());
    }

    @Test
    void shouldConvertEntityToResponseDTO() {
        // When
        ReservaResponseDTO result = reservaMapper.toResponseDTO(reserva);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertNotNull(result.getDataCriacao());
        assertEquals(LocalDate.now().plusDays(30), result.getDataEvento());
        assertEquals(new BigDecimal("500.00"), result.getValorTotal());
        assertEquals("Observação teste", result.getObservacoes());
        assertEquals(StatusReservaEnum.AGUARDANDO_SINAL, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getTotalPago());
        assertEquals(new BigDecimal("500.00"), result.getSaldo());
    }

    @Test
    void shouldCalculateTotalPagoCorrectly() {
        // Given
        List<Pagamento> pagamentos = new ArrayList<>();
        pagamentos.add(Pagamento.builder()
                .valor(new BigDecimal("200.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .build());
        pagamentos.add(Pagamento.builder()
                .valor(new BigDecimal("300.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .build());
        reserva.setPagamentos(pagamentos);

        // When
        ReservaResponseDTO result = reservaMapper.toResponseDTO(reserva);

        // Then
        assertEquals(0, new BigDecimal("500.00").compareTo(result.getTotalPago()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getSaldo()));
    }

    @Test
    void shouldCalculateSaldoCorrectly() {
        // Given
        List<Pagamento> pagamentos = new ArrayList<>();
        pagamentos.add(Pagamento.builder()
                .valor(new BigDecimal("200.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .build());
        reserva.setPagamentos(pagamentos);

        // When
        ReservaResponseDTO result = reservaMapper.toResponseDTO(reserva);

        // Then
        assertEquals(new BigDecimal("200.00"), result.getTotalPago());
        assertEquals(new BigDecimal("300.00"), result.getSaldo());
    }

    @Test
    void shouldHandleEmptyPagamentosList() {
        // Given
        reserva.setPagamentos(new ArrayList<>());

        // When
        ReservaResponseDTO result = reservaMapper.toResponseDTO(reserva);

        // Then
        assertEquals(BigDecimal.ZERO, result.getTotalPago());
        assertEquals(new BigDecimal("500.00"), result.getSaldo());
    }

}

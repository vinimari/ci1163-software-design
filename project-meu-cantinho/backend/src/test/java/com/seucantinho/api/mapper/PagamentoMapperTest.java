package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.dto.pagamento.PagamentoRequestDTO;
import com.seucantinho.api.dto.pagamento.PagamentoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoMapperTest {

    private PagamentoMapper pagamentoMapper;
    private PagamentoRequestDTO requestDTO;
    private Pagamento pagamento;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        pagamentoMapper = new PagamentoMapper();

        reserva = Reserva.builder()
                .id(1)
                .build();

        requestDTO = new PagamentoRequestDTO(
                new BigDecimal("500.00"),
                TipoPagamentoEnum.SINAL,
                "PIX",
                "TRX123456",
                1
        );

        pagamento = Pagamento.builder()
                .id(1)
                .dataPagamento(LocalDateTime.now())
                .valor(new BigDecimal("500.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .codigoTransacaoGateway("TRX123456")
                .reserva(reserva)
                .build();
    }

    @Test
    void shouldConvertRequestDTOToEntity() {
        // When
        Pagamento result = pagamentoMapper.toEntity(requestDTO, reserva);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getValor());
        assertEquals(TipoPagamentoEnum.SINAL, result.getTipo());
        assertEquals("PIX", result.getFormaPagamento());
        assertEquals("TRX123456", result.getCodigoTransacaoGateway());
        assertEquals(reserva, result.getReserva());
    }

    @Test
    void shouldConvertEntityToResponseDTO() {
        // When
        PagamentoResponseDTO result = pagamentoMapper.toResponseDTO(pagamento);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertNotNull(result.getDataPagamento());
        assertEquals(new BigDecimal("500.00"), result.getValor());
        assertEquals(TipoPagamentoEnum.SINAL, result.getTipo());
        assertEquals("PIX", result.getFormaPagamento());
        assertEquals("TRX123456", result.getCodigoTransacaoGateway());
        assertEquals(1, result.getReservaId());
    }

    @Test
    void shouldHandleNullFormaPagamento() {
        // Given
        requestDTO.setFormaPagamento(null);

        // When
        Pagamento result = pagamentoMapper.toEntity(requestDTO, reserva);

        // Then
        assertNull(result.getFormaPagamento());
    }

    @Test
    void shouldHandleNullCodigoTransacao() {
        // Given
        requestDTO.setCodigoTransacaoGateway(null);

        // When
        Pagamento result = pagamentoMapper.toEntity(requestDTO, reserva);

        // Then
        assertNull(result.getCodigoTransacaoGateway());
    }

    @Test
    void shouldMapDifferentPaymentTypes() {
        // Given
        requestDTO.setTipo(TipoPagamentoEnum.QUITACAO);

        // When
        Pagamento result = pagamentoMapper.toEntity(requestDTO, reserva);

        // Then
        assertEquals(TipoPagamentoEnum.QUITACAO, result.getTipo());
    }
}

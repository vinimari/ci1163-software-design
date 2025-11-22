package com.seucantinho.api.dto.pagamento;

import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoResponseDTOTest {

    @Test
    void shouldCreatePagamentoResponseDTOWithBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        PagamentoResponseDTO dto = PagamentoResponseDTO.builder()
                .id(1)
                .dataPagamento(now)
                .valor(new BigDecimal("500.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .codigoTransacaoGateway("TRX123456")
                .reservaId(1)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(now, dto.getDataPagamento());
        assertEquals(new BigDecimal("500.00"), dto.getValor());
        assertEquals(TipoPagamentoEnum.SINAL, dto.getTipo());
        assertEquals("PIX", dto.getFormaPagamento());
        assertEquals("TRX123456", dto.getCodigoTransacaoGateway());
        assertEquals(1, dto.getReservaId());
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        PagamentoResponseDTO dto = new PagamentoResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        // When
        dto.setId(1);
        dto.setDataPagamento(now);
        dto.setValor(new BigDecimal("500.00"));
        dto.setTipo(TipoPagamentoEnum.SINAL);
        dto.setFormaPagamento("PIX");
        dto.setCodigoTransacaoGateway("TRX123456");
        dto.setReservaId(1);

        // Then
        assertEquals(1, dto.getId());
        assertEquals(now, dto.getDataPagamento());
        assertEquals(new BigDecimal("500.00"), dto.getValor());
        assertEquals(TipoPagamentoEnum.SINAL, dto.getTipo());
        assertEquals("PIX", dto.getFormaPagamento());
        assertEquals("TRX123456", dto.getCodigoTransacaoGateway());
        assertEquals(1, dto.getReservaId());
    }

    @Test
    void shouldUseAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        PagamentoResponseDTO dto = new PagamentoResponseDTO(
                1,
                now,
                new BigDecimal("500.00"),
                TipoPagamentoEnum.QUITACAO,
                "Cartão de Crédito",
                "TRX999",
                2
        );

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(TipoPagamentoEnum.QUITACAO, dto.getTipo());
        assertEquals(2, dto.getReservaId());
    }
}

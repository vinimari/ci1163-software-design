package com.seucantinho.api.dto.pagamento;

import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidPagamentoRequestDTO() {
        // Given
        PagamentoRequestDTO dto = new PagamentoRequestDTO(
                new BigDecimal("500.00"),
                TipoPagamentoEnum.SINAL,
                "PIX",
                "TRX123456",
                1
        );

        // When
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenValorIsZero() {
        // Given
        PagamentoRequestDTO dto = new PagamentoRequestDTO(
                BigDecimal.ZERO,
                TipoPagamentoEnum.SINAL,
                "PIX",
                "TRX123456",
                1
        );

        // When
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("valor")));
    }

    @Test
    void shouldFailWhenTipoIsNull() {
        // Given
        PagamentoRequestDTO dto = new PagamentoRequestDTO(
                new BigDecimal("500.00"),
                null,
                "PIX",
                "TRX123456",
                1
        );

        // When
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("tipo")));
    }

    @Test
    void shouldFailWhenReservaIdIsNull() {
        // Given
        PagamentoRequestDTO dto = new PagamentoRequestDTO(
                new BigDecimal("500.00"),
                TipoPagamentoEnum.SINAL,
                "PIX",
                "TRX123456",
                null
        );

        // When
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("reservaId")));
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        PagamentoRequestDTO dto = new PagamentoRequestDTO();

        // When
        dto.setValor(new BigDecimal("500.00"));
        dto.setTipo(TipoPagamentoEnum.SINAL);
        dto.setFormaPagamento("PIX");
        dto.setCodigoTransacaoGateway("TRX123456");
        dto.setReservaId(1);

        // Then
        assertEquals(new BigDecimal("500.00"), dto.getValor());
        assertEquals(TipoPagamentoEnum.SINAL, dto.getTipo());
        assertEquals("PIX", dto.getFormaPagamento());
        assertEquals("TRX123456", dto.getCodigoTransacaoGateway());
        assertEquals(1, dto.getReservaId());
    }

    @Test
    void shouldAllowNullOptionalFields() {
        // Given
        PagamentoRequestDTO dto = new PagamentoRequestDTO(
                new BigDecimal("500.00"),
                TipoPagamentoEnum.SINAL,
                null,
                null,
                1
        );

        // When
        Set<ConstraintViolation<PagamentoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldUseBuilder() {
        // When
        PagamentoRequestDTO dto = PagamentoRequestDTO.builder()
                .valor(new BigDecimal("500.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .formaPagamento("Cartão de Crédito")
                .codigoTransacaoGateway("TRX999")
                .reservaId(2)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(new BigDecimal("500.00"), dto.getValor());
        assertEquals(TipoPagamentoEnum.QUITACAO, dto.getTipo());
        assertEquals("Cartão de Crédito", dto.getFormaPagamento());
    }
}

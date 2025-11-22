package com.seucantinho.api.dto.reserva;

import com.seucantinho.api.domain.enums.StatusReservaEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReservaRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidReservaRequestDTO() {
        // Given
        ReservaRequestDTO dto = new ReservaRequestDTO(
                LocalDate.now().plusDays(30),
                new BigDecimal("500.00"),
                "Observação teste",
                StatusReservaEnum.AGUARDANDO_SINAL,
                1,
                1
        );

        // When
        Set<ConstraintViolation<ReservaRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDataEventoIsPast() {
        // Given
        ReservaRequestDTO dto = new ReservaRequestDTO(
                LocalDate.now().minusDays(1),
                new BigDecimal("500.00"),
                "Observação teste",
                StatusReservaEnum.AGUARDANDO_SINAL,
                1,
                1
        );

        // When
        Set<ConstraintViolation<ReservaRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dataEvento")));
    }

    @Test
    void shouldFailWhenValorTotalIsZero() {
        // Given
        ReservaRequestDTO dto = new ReservaRequestDTO(
                LocalDate.now().plusDays(30),
                BigDecimal.ZERO,
                "Observação teste",
                StatusReservaEnum.AGUARDANDO_SINAL,
                1,
                1
        );

        // When
        Set<ConstraintViolation<ReservaRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("valorTotal")));
    }

    @Test
    void shouldFailWhenUsuarioIdIsNull() {
        // Given
        ReservaRequestDTO dto = new ReservaRequestDTO(
                LocalDate.now().plusDays(30),
                new BigDecimal("500.00"),
                "Observação teste",
                StatusReservaEnum.AGUARDANDO_SINAL,
                null,
                1
        );

        // When
        Set<ConstraintViolation<ReservaRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("usuarioId")));
    }

    @Test
    void shouldFailWhenEspacoIdIsNull() {
        // Given
        ReservaRequestDTO dto = new ReservaRequestDTO(
                LocalDate.now().plusDays(30),
                new BigDecimal("500.00"),
                "Observação teste",
                StatusReservaEnum.AGUARDANDO_SINAL,
                1,
                null
        );

        // When
        Set<ConstraintViolation<ReservaRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("espacoId")));
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        ReservaRequestDTO dto = new ReservaRequestDTO();
        LocalDate futureDate = LocalDate.now().plusDays(30);

        // When
        dto.setDataEvento(futureDate);
        dto.setValorTotal(new BigDecimal("500.00"));
        dto.setObservacoes("Observação teste");
        dto.setStatus(StatusReservaEnum.CONFIRMADA);
        dto.setUsuarioId(1);
        dto.setEspacoId(1);

        // Then
        assertEquals(futureDate, dto.getDataEvento());
        assertEquals(new BigDecimal("500.00"), dto.getValorTotal());
        assertEquals("Observação teste", dto.getObservacoes());
        assertEquals(StatusReservaEnum.CONFIRMADA, dto.getStatus());
        assertEquals(1, dto.getUsuarioId());
        assertEquals(1, dto.getEspacoId());
    }

    @Test
    void shouldAllowNullOptionalFields() {
        // Given
        ReservaRequestDTO dto = new ReservaRequestDTO(
                LocalDate.now().plusDays(30),
                new BigDecimal("500.00"),
                null,
                null,
                1,
                1
        );

        // When
        Set<ConstraintViolation<ReservaRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldUseBuilder() {
        // When
        ReservaRequestDTO dto = ReservaRequestDTO.builder()
                .dataEvento(LocalDate.now().plusDays(60))
                .valorTotal(new BigDecimal("1000.00"))
                .observacoes("Observação builder")
                .status(StatusReservaEnum.QUITADA)
                .usuarioId(2)
                .espacoId(3)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(LocalDate.now().plusDays(60), dto.getDataEvento());
        assertEquals(new BigDecimal("1000.00"), dto.getValorTotal());
        assertEquals(StatusReservaEnum.QUITADA, dto.getStatus());
    }
}

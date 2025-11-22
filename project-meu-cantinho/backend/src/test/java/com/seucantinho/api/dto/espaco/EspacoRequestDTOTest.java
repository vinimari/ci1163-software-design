package com.seucantinho.api.dto.espaco;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EspacoRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidEspacoRequestDTO() {
        // Given
        EspacoRequestDTO dto = new EspacoRequestDTO(
                "Salão Principal",
                "Espaço amplo",
                100,
                new BigDecimal("500.00"),
                true,
                "foto.jpg",
                1
        );

        // When
        Set<ConstraintViolation<EspacoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNomeIsBlank() {
        // Given
        EspacoRequestDTO dto = new EspacoRequestDTO(
                "",
                "Espaço amplo",
                100,
                new BigDecimal("500.00"),
                true,
                "foto.jpg",
                1
        );

        // When
        Set<ConstraintViolation<EspacoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    void shouldFailWhenCapacidadeIsZero() {
        // Given
        EspacoRequestDTO dto = new EspacoRequestDTO(
                "Salão Principal",
                "Espaço amplo",
                0,
                new BigDecimal("500.00"),
                true,
                "foto.jpg",
                1
        );

        // When
        Set<ConstraintViolation<EspacoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("capacidade")));
    }

    @Test
    void shouldFailWhenPrecoDiariaIsNegative() {
        // Given
        EspacoRequestDTO dto = new EspacoRequestDTO(
                "Salão Principal",
                "Espaço amplo",
                100,
                new BigDecimal("-100.00"),
                true,
                "foto.jpg",
                1
        );

        // When
        Set<ConstraintViolation<EspacoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("precoDiaria")));
    }

    @Test
    void shouldFailWhenFilialIdIsNull() {
        // Given
        EspacoRequestDTO dto = new EspacoRequestDTO(
                "Salão Principal",
                "Espaço amplo",
                100,
                new BigDecimal("500.00"),
                true,
                "foto.jpg",
                null
        );

        // When
        Set<ConstraintViolation<EspacoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("filialId")));
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        EspacoRequestDTO dto = new EspacoRequestDTO();

        // When
        dto.setNome("Salão Principal");
        dto.setDescricao("Espaço amplo");
        dto.setCapacidade(100);
        dto.setPrecoDiaria(new BigDecimal("500.00"));
        dto.setAtivo(true);
        dto.setUrlFotoPrincipal("foto.jpg");
        dto.setFilialId(1);

        // Then
        assertEquals("Salão Principal", dto.getNome());
        assertEquals("Espaço amplo", dto.getDescricao());
        assertEquals(100, dto.getCapacidade());
        assertEquals(new BigDecimal("500.00"), dto.getPrecoDiaria());
        assertTrue(dto.getAtivo());
        assertEquals("foto.jpg", dto.getUrlFotoPrincipal());
        assertEquals(1, dto.getFilialId());
    }

    @Test
    void shouldAllowNullOptionalFields() {
        // Given
        EspacoRequestDTO dto = new EspacoRequestDTO(
                "Salão Principal",
                null,
                100,
                new BigDecimal("500.00"),
                null,
                null,
                1
        );

        // When
        Set<ConstraintViolation<EspacoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }
}

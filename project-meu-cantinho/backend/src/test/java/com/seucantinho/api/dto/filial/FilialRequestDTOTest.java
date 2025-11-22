package com.seucantinho.api.dto.filial;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilialRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidFilialRequestDTO() {
        // Given
        FilialRequestDTO dto = new FilialRequestDTO(
                "Filial Centro",
                "Curitiba",
                "PR",
                "Rua A, 123",
                "41999998888"
        );

        // When
        Set<ConstraintViolation<FilialRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNomeIsBlank() {
        // Given
        FilialRequestDTO dto = new FilialRequestDTO(
                "",
                "Curitiba",
                "PR",
                "Rua A, 123",
                "41999998888"
        );

        // When
        Set<ConstraintViolation<FilialRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    void shouldFailWhenCidadeIsBlank() {
        // Given
        FilialRequestDTO dto = new FilialRequestDTO(
                "Filial Centro",
                "",
                "PR",
                "Rua A, 123",
                "41999998888"
        );

        // When
        Set<ConstraintViolation<FilialRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cidade")));
    }

    @Test
    void shouldFailWhenEstadoIsInvalid() {
        // Given
        FilialRequestDTO dto = new FilialRequestDTO(
                "Filial Centro",
                "Curitiba",
                "P",
                "Rua A, 123",
                "41999998888"
        );

        // When
        Set<ConstraintViolation<FilialRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("estado")));
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        FilialRequestDTO dto = new FilialRequestDTO();

        // When
        dto.setNome("Filial Centro");
        dto.setCidade("Curitiba");
        dto.setEstado("PR");
        dto.setEndereco("Rua A, 123");
        dto.setTelefone("41999998888");

        // Then
        assertEquals("Filial Centro", dto.getNome());
        assertEquals("Curitiba", dto.getCidade());
        assertEquals("PR", dto.getEstado());
        assertEquals("Rua A, 123", dto.getEndereco());
        assertEquals("41999998888", dto.getTelefone());
    }

    @Test
    void shouldAllowNullOptionalFields() {
        // Given
        FilialRequestDTO dto = new FilialRequestDTO(
                "Filial Centro",
                "Curitiba",
                "PR",
                null,
                null
        );

        // When
        Set<ConstraintViolation<FilialRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }
}

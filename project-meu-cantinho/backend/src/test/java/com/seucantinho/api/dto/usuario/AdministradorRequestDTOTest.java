package com.seucantinho.api.dto.usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdministradorRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidAdministradorRequestDTO() {
        // Given
        AdministradorRequestDTO dto = AdministradorRequestDTO.builder()
                .nome("Admin Silva")
                .email("admin@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .build();

        // When
        Set<ConstraintViolation<AdministradorRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNomeIsBlank() {
        // Given
        AdministradorRequestDTO dto = AdministradorRequestDTO.builder()
                .nome("")
                .email("admin@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .build();

        // When
        Set<ConstraintViolation<AdministradorRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        // Given
        AdministradorRequestDTO dto = AdministradorRequestDTO.builder()
                .nome("Admin Silva")
                .email("email-invalido")
                .senha("senha123")
                .cpf("12345678900")
                .build();

        // When
        Set<ConstraintViolation<AdministradorRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        AdministradorRequestDTO dto = new AdministradorRequestDTO();

        // When
        dto.setNome("Admin Silva");
        dto.setEmail("admin@email.com");
        dto.setSenha("senha123");
        dto.setCpf("12345678900");
        dto.setTelefone("41999998888");
        dto.setAtivo(true);

        // Then
        assertEquals("Admin Silva", dto.getNome());
        assertEquals("admin@email.com", dto.getEmail());
        assertEquals("senha123", dto.getSenha());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
    }

    @Test
    void shouldUseBuilder() {
        // When
        AdministradorRequestDTO dto = AdministradorRequestDTO.builder()
                .nome("Admin Silva")
                .email("admin@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(false)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals("Admin Silva", dto.getNome());
        assertEquals("admin@email.com", dto.getEmail());
        assertFalse(dto.getAtivo());
    }

    @Test
    void shouldHaveAdminPerfil() {
        // Given
        AdministradorRequestDTO dto = AdministradorRequestDTO.builder()
                .nome("Admin Silva")
                .email("admin@email.com")
                .senha("senha123")
                .build();

        // Then
        assertEquals(com.seucantinho.api.domain.enums.PerfilUsuarioEnum.ADMIN, dto.getPerfil());
    }
}

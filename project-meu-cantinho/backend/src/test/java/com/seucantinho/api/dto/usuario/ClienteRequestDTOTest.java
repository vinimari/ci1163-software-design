package com.seucantinho.api.dto.usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClienteRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidClienteRequestDTO() {
        // Given
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .build();

        // When
        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNomeIsBlank() {
        // Given
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("")
                .email("joao@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .build();

        // When
        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        // Given
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("email-invalido")
                .senha("senha123")
                .cpf("12345678900")
                .build();

        // When
        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenSenhaIsTooShort() {
        // Given
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("12345")
                .cpf("12345678900")
                .build();

        // When
        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    void shouldFailWhenCpfExceedsMaxLength() {
        // Given
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .cpf("123456789012345")  // 15 caracteres, excede o máximo de 14
                .build();

        // When
        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        ClienteRequestDTO dto = new ClienteRequestDTO();

        // When
        dto.setNome("João Silva");
        dto.setEmail("joao@email.com");
        dto.setSenha("senha123");
        dto.setCpf("12345678900");
        dto.setTelefone("41999998888");
        dto.setAtivo(true);

        // Then
        assertEquals("João Silva", dto.getNome());
        assertEquals("joao@email.com", dto.getEmail());
        assertEquals("senha123", dto.getSenha());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
    }

    @Test
    void shouldUseBuilder() {
        // When
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(false)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals("João Silva", dto.getNome());
        assertEquals("joao@email.com", dto.getEmail());
        assertEquals("senha123", dto.getSenha());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertFalse(dto.getAtivo());
    }
}

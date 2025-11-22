package com.seucantinho.api.dto.usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidFuncionarioRequestDTO() {
        // Given
        FuncionarioRequestDTO dto = FuncionarioRequestDTO.builder()
                .nome("Maria Funcionária")
                .email("maria@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .matricula("MAT001")
                .filialId(1)
                .build();

        // When
        Set<ConstraintViolation<FuncionarioRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNomeIsBlank() {
        // Given
        FuncionarioRequestDTO dto = FuncionarioRequestDTO.builder()
                .nome("")
                .email("maria@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .matricula("MAT001")
                .filialId(1)
                .build();

        // When
        Set<ConstraintViolation<FuncionarioRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    void shouldFailWhenMatriculaIsBlank() {
        // Given
        FuncionarioRequestDTO dto = FuncionarioRequestDTO.builder()
                .nome("Maria Funcionária")
                .email("maria@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .matricula("")
                .filialId(1)
                .build();

        // When
        Set<ConstraintViolation<FuncionarioRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("matricula")));
    }

    @Test
    void shouldFailWhenFilialIdIsNull() {
        // Given
        FuncionarioRequestDTO dto = FuncionarioRequestDTO.builder()
                .nome("Maria Funcionária")
                .email("maria@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .matricula("MAT001")
                .filialId(null)
                .build();

        // When
        Set<ConstraintViolation<FuncionarioRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("filialId")));
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        // Given
        FuncionarioRequestDTO dto = FuncionarioRequestDTO.builder()
                .nome("Maria Funcionária")
                .email("email-invalido")
                .senha("senha123")
                .cpf("12345678900")
                .matricula("MAT001")
                .filialId(1)
                .build();

        // When
        Set<ConstraintViolation<FuncionarioRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        FuncionarioRequestDTO dto = new FuncionarioRequestDTO();

        // When
        dto.setNome("Maria Funcionária");
        dto.setEmail("maria@email.com");
        dto.setSenha("senha123");
        dto.setCpf("12345678900");
        dto.setTelefone("41999998888");
        dto.setAtivo(true);
        dto.setMatricula("MAT001");
        dto.setFilialId(1);

        // Then
        assertEquals("Maria Funcionária", dto.getNome());
        assertEquals("maria@email.com", dto.getEmail());
        assertEquals("senha123", dto.getSenha());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
        assertEquals("MAT001", dto.getMatricula());
        assertEquals(1, dto.getFilialId());
    }

    @Test
    void shouldUseBuilder() {
        // When
        FuncionarioRequestDTO dto = FuncionarioRequestDTO.builder()
                .nome("Maria Funcionária")
                .email("maria@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(false)
                .matricula("MAT001")
                .filialId(1)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals("Maria Funcionária", dto.getNome());
        assertEquals("maria@email.com", dto.getEmail());
        assertEquals("MAT001", dto.getMatricula());
        assertEquals(1, dto.getFilialId());
        assertFalse(dto.getAtivo());
    }

    @Test
    void shouldHaveFuncionarioPerfil() {
        // Given
        FuncionarioRequestDTO dto = FuncionarioRequestDTO.builder()
                .nome("Maria Funcionária")
                .email("maria@email.com")
                .senha("senha123")
                .matricula("MAT001")
                .filialId(1)
                .build();

        // Then
        assertEquals(com.seucantinho.api.domain.enums.PerfilUsuarioEnum.FUNCIONARIO, dto.getPerfil());
    }
}

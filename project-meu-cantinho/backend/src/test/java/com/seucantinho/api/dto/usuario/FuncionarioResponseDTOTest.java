package com.seucantinho.api.dto.usuario;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioResponseDTOTest {

    @Test
    void shouldCreateFuncionarioResponseDTOWithBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        FilialResponseDTO filialDTO = FilialResponseDTO.builder()
                .id(1)
                .nome("Filial Centro")
                .build();

        // When
        FuncionarioResponseDTO dto = FuncionarioResponseDTO.builder()
                .id(1)
                .nome("Maria Funcionária")
                .email("maria@email.com")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .dataCadastro(now)
                .matricula("MAT001")
                .filial(filialDTO)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Maria Funcionária", dto.getNome());
        assertEquals("maria@email.com", dto.getEmail());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
        assertEquals(now, dto.getDataCadastro());
        assertEquals("MAT001", dto.getMatricula());
        assertNotNull(dto.getFilial());
        assertEquals(1, dto.getFilial().getId());
        assertEquals(PerfilUsuarioEnum.FUNCIONARIO, dto.getPerfil());
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        FuncionarioResponseDTO dto = new FuncionarioResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        FilialResponseDTO filialDTO = FilialResponseDTO.builder()
                .id(1)
                .nome("Filial Centro")
                .build();

        // When
        dto.setId(1);
        dto.setNome("Maria Funcionária");
        dto.setEmail("maria@email.com");
        dto.setCpf("12345678900");
        dto.setTelefone("41999998888");
        dto.setAtivo(true);
        dto.setDataCadastro(now);
        dto.setMatricula("MAT001");
        dto.setFilial(filialDTO);

        // Then
        assertEquals(1, dto.getId());
        assertEquals("Maria Funcionária", dto.getNome());
        assertEquals("maria@email.com", dto.getEmail());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
        assertEquals(now, dto.getDataCadastro());
        assertEquals("MAT001", dto.getMatricula());
        assertNotNull(dto.getFilial());
        assertEquals(1, dto.getFilial().getId());
        // Perfil não é definido com construtor padrão, apenas com builder
    }

    @Test
    void shouldHaveFuncionarioPerfil() {
        // When
        FuncionarioResponseDTO dto = FuncionarioResponseDTO.builder()
                .id(1)
                .nome("Maria Funcionária")
                .email("maria@email.com")
                .matricula("MAT001")
                .build();

        // Then
        assertEquals(PerfilUsuarioEnum.FUNCIONARIO, dto.getPerfil());
    }

    @Test
    void shouldHandleNullFilial() {
        // When
        FuncionarioResponseDTO dto = FuncionarioResponseDTO.builder()
                .id(1)
                .nome("Maria Funcionária")
                .email("maria@email.com")
                .matricula("MAT001")
                .filial(null)
                .build();

        // Then
        assertNotNull(dto);
        assertNull(dto.getFilial());
    }
}

package com.seucantinho.api.dto.filial;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FilialResponseDTOTest {

    @Test
    void shouldCreateFilialResponseDTOWithBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        FilialResponseDTO dto = FilialResponseDTO.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua A, 123")
                .telefone("41999998888")
                .dataCadastro(now)
                .quantidadeEspacos(3)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Filial Centro", dto.getNome());
        assertEquals("Curitiba", dto.getCidade());
        assertEquals("PR", dto.getEstado());
        assertEquals("Rua A, 123", dto.getEndereco());
        assertEquals("41999998888", dto.getTelefone());
        assertEquals(now, dto.getDataCadastro());
        assertEquals(3, dto.getQuantidadeEspacos());
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        FilialResponseDTO dto = new FilialResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        // When
        dto.setId(1);
        dto.setNome("Filial Centro");
        dto.setCidade("Curitiba");
        dto.setEstado("PR");
        dto.setEndereco("Rua A, 123");
        dto.setTelefone("41999998888");
        dto.setDataCadastro(now);
        dto.setQuantidadeEspacos(3);

        // Then
        assertEquals(1, dto.getId());
        assertEquals("Filial Centro", dto.getNome());
        assertEquals("Curitiba", dto.getCidade());
        assertEquals("PR", dto.getEstado());
        assertEquals("Rua A, 123", dto.getEndereco());
        assertEquals("41999998888", dto.getTelefone());
        assertEquals(now, dto.getDataCadastro());
        assertEquals(3, dto.getQuantidadeEspacos());
    }

    @Test
    void shouldUseAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        FilialResponseDTO dto = new FilialResponseDTO(
                1,
                "Filial Centro",
                "Curitiba",
                "PR",
                "Rua A, 123",
                "41999998888",
                now,
                3
        );

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Filial Centro", dto.getNome());
        assertEquals(3, dto.getQuantidadeEspacos());
    }
}

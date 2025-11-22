package com.seucantinho.api.dto.usuario;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClienteResponseDTOTest {

    @Test
    void shouldCreateClienteResponseDTOWithBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ClienteResponseDTO dto = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .dataCadastro(now)
                .quantidadeReservas(5)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("João Silva", dto.getNome());
        assertEquals("joao@email.com", dto.getEmail());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
        assertEquals(now, dto.getDataCadastro());
        assertEquals(5, dto.getQuantidadeReservas());
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        ClienteResponseDTO dto = new ClienteResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        // When
        dto.setId(1);
        dto.setNome("João Silva");
        dto.setEmail("joao@email.com");
        dto.setCpf("12345678900");
        dto.setTelefone("41999998888");
        dto.setAtivo(true);
        dto.setDataCadastro(now);
        dto.setQuantidadeReservas(5);

        // Then
        assertEquals(1, dto.getId());
        assertEquals("João Silva", dto.getNome());
        assertEquals("joao@email.com", dto.getEmail());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
        assertEquals(now, dto.getDataCadastro());
        assertEquals(5, dto.getQuantidadeReservas());
    }

    @Test
    void shouldHandleNullValues() {
        // When
        ClienteResponseDTO dto = ClienteResponseDTO.builder()
                .id(null)
                .nome(null)
                .email(null)
                .cpf(null)
                .telefone(null)
                .ativo(null)
                .dataCadastro(null)
                .quantidadeReservas(null)
                .build();

        // Then
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getEmail());
    }
}

package com.seucantinho.api.dto.usuario;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AdministradorResponseDTOTest {

    @Test
    void shouldCreateAdministradorResponseDTOWithBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        AdministradorResponseDTO dto = AdministradorResponseDTO.builder()
                .id(1)
                .nome("Admin Silva")
                .email("admin@email.com")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .dataCadastro(now)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Admin Silva", dto.getNome());
        assertEquals("admin@email.com", dto.getEmail());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
        assertEquals(now, dto.getDataCadastro());
        assertEquals(PerfilUsuarioEnum.ADMIN, dto.getPerfil());
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        AdministradorResponseDTO dto = new AdministradorResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        // When
        dto.setId(1);
        dto.setNome("Admin Silva");
        dto.setEmail("admin@email.com");
        dto.setCpf("12345678900");
        dto.setTelefone("41999998888");
        dto.setAtivo(true);
        dto.setDataCadastro(now);

        // Then
        assertEquals(1, dto.getId());
        assertEquals("Admin Silva", dto.getNome());
        assertEquals("admin@email.com", dto.getEmail());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("41999998888", dto.getTelefone());
        assertTrue(dto.getAtivo());
        assertEquals(now, dto.getDataCadastro());
        // Perfil não é definido com construtor padrão, apenas com builder
    }

    @Test
    void shouldHaveAdminPerfil() {
        // When
        AdministradorResponseDTO dto = AdministradorResponseDTO.builder()
                .id(1)
                .nome("Admin Silva")
                .email("admin@email.com")
                .build();

        // Then
        assertEquals(PerfilUsuarioEnum.ADMIN, dto.getPerfil());
    }
}

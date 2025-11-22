package com.seucantinho.api.dto.espaco;

import com.seucantinho.api.dto.filial.FilialResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EspacoResponseDTOTest {

    @Test
    void shouldCreateEspacoResponseDTOWithBuilder() {
        // Given
        FilialResponseDTO filialDTO = FilialResponseDTO.builder()
                .id(1)
                .nome("Filial Centro")
                .build();

        // When
        EspacoResponseDTO dto = EspacoResponseDTO.builder()
                .id(1)
                .nome("Salão Principal")
                .descricao("Espaço amplo")
                .capacidade(100)
                .precoDiaria(new BigDecimal("500.00"))
                .ativo(true)
                .urlFotoPrincipal("foto.jpg")
                .filial(filialDTO)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Salão Principal", dto.getNome());
        assertEquals("Espaço amplo", dto.getDescricao());
        assertEquals(100, dto.getCapacidade());
        assertEquals(new BigDecimal("500.00"), dto.getPrecoDiaria());
        assertTrue(dto.getAtivo());
        assertEquals("foto.jpg", dto.getUrlFotoPrincipal());
        assertNotNull(dto.getFilial());
        assertEquals(1, dto.getFilial().getId());
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        EspacoResponseDTO dto = new EspacoResponseDTO();
        FilialResponseDTO filialDTO = new FilialResponseDTO();

        // When
        dto.setId(1);
        dto.setNome("Salão Principal");
        dto.setDescricao("Espaço amplo");
        dto.setCapacidade(100);
        dto.setPrecoDiaria(new BigDecimal("500.00"));
        dto.setAtivo(true);
        dto.setUrlFotoPrincipal("foto.jpg");
        dto.setFilial(filialDTO);

        // Then
        assertEquals(1, dto.getId());
        assertEquals("Salão Principal", dto.getNome());
        assertEquals("Espaço amplo", dto.getDescricao());
        assertEquals(100, dto.getCapacidade());
        assertEquals(new BigDecimal("500.00"), dto.getPrecoDiaria());
        assertTrue(dto.getAtivo());
        assertEquals("foto.jpg", dto.getUrlFotoPrincipal());
        assertNotNull(dto.getFilial());
    }

    @Test
    void shouldUseAllArgsConstructor() {
        // Given
        FilialResponseDTO filialDTO = new FilialResponseDTO();

        // When
        EspacoResponseDTO dto = new EspacoResponseDTO(
                1,
                "Salão Principal",
                "Espaço amplo",
                100,
                new BigDecimal("500.00"),
                true,
                "foto.jpg",
                filialDTO
        );

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Salão Principal", dto.getNome());
        assertNotNull(dto.getFilial());
    }
}

package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Filial;
import com.seucantinho.api.dto.espaco.EspacoRequestDTO;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EspacoMapperTest {

    private EspacoMapper espacoMapper;
    private EspacoRequestDTO requestDTO;
    private Espaco espaco;
    private Filial filial;

    @BeforeEach
    void setUp() {
        espacoMapper = new EspacoMapper();

        filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua A, 123")
                .telefone("41999998888")
                .dataCadastro(LocalDateTime.now())
                .build();

        requestDTO = new EspacoRequestDTO(
                "Salão Principal",
                "Espaço amplo e moderno",
                100,
                new BigDecimal("500.00"),
                true,
                "foto.jpg",
                1
        );

        espaco = Espaco.builder()
                .id(1)
                .nome("Salão Principal")
                .descricao("Espaço amplo e moderno")
                .capacidade(100)
                .precoDiaria(new BigDecimal("500.00"))
                .ativo(true)
                .urlFotoPrincipal("foto.jpg")
                .filial(filial)
                .build();
    }

    @Test
    void shouldConvertRequestDTOToEntity() {
        // When
        Espaco result = espacoMapper.toEntity(requestDTO, filial);

        // Then
        assertNotNull(result);
        assertEquals("Salão Principal", result.getNome());
        assertEquals("Espaço amplo e moderno", result.getDescricao());
        assertEquals(100, result.getCapacidade());
        assertEquals(new BigDecimal("500.00"), result.getPrecoDiaria());
        assertTrue(result.getAtivo());
        assertEquals("foto.jpg", result.getUrlFotoPrincipal());
        assertEquals(filial, result.getFilial());
    }

    @Test
    void shouldSetAtivoToTrueWhenNull() {
        // Given
        requestDTO.setAtivo(null);

        // When
        Espaco result = espacoMapper.toEntity(requestDTO, filial);

        // Then
        assertTrue(result.getAtivo());
    }

    @Test
    void shouldConvertEntityToResponseDTO() {
        // When
        EspacoResponseDTO result = espacoMapper.toResponseDTO(espaco);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Salão Principal", result.getNome());
        assertEquals("Espaço amplo e moderno", result.getDescricao());
        assertEquals(100, result.getCapacidade());
        assertEquals(new BigDecimal("500.00"), result.getPrecoDiaria());
        assertTrue(result.getAtivo());
        assertEquals("foto.jpg", result.getUrlFotoPrincipal());

        // Verificar filial DTO
        assertNotNull(result.getFilial());
        assertEquals(1, result.getFilial().getId());
        assertEquals("Filial Centro", result.getFilial().getNome());
        assertEquals("Curitiba", result.getFilial().getCidade());
        assertEquals("PR", result.getFilial().getEstado());
    }

    @Test
    void shouldUpdateEntityFromDTO() {
        // Given
        Filial newFilial = Filial.builder()
                .id(2)
                .nome("Filial Batel")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        EspacoRequestDTO updateDTO = new EspacoRequestDTO(
                "Salão Atualizado",
                "Nova descrição",
                150,
                new BigDecimal("700.00"),
                false,
                "nova-foto.jpg",
                2
        );

        // When
        espacoMapper.updateEntityFromDTO(espaco, updateDTO, newFilial);

        // Then
        assertEquals("Salão Atualizado", espaco.getNome());
        assertEquals("Nova descrição", espaco.getDescricao());
        assertEquals(150, espaco.getCapacidade());
        assertEquals(new BigDecimal("700.00"), espaco.getPrecoDiaria());
        assertFalse(espaco.getAtivo());
        assertEquals("nova-foto.jpg", espaco.getUrlFotoPrincipal());
        assertEquals(newFilial, espaco.getFilial());
    }

    @Test
    void shouldPreserveIdWhenUpdating() {
        // Given
        Integer originalId = espaco.getId();
        Filial newFilial = Filial.builder().id(2).nome("Nova Filial").build();
        EspacoRequestDTO updateDTO = new EspacoRequestDTO(
                "Salão Atualizado",
                "Nova descrição",
                150,
                new BigDecimal("700.00"),
                false,
                "nova-foto.jpg",
                2
        );

        // When
        espacoMapper.updateEntityFromDTO(espaco, updateDTO, newFilial);

        // Then
        assertEquals(originalId, espaco.getId());
    }
}

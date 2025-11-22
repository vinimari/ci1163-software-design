package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Filial;
import com.seucantinho.api.dto.filial.FilialRequestDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FilialMapperTest {

    private FilialMapper filialMapper;
    private FilialRequestDTO requestDTO;
    private Filial filial;

    @BeforeEach
    void setUp() {
        filialMapper = new FilialMapper();

        requestDTO = new FilialRequestDTO(
                "Filial Centro",
                "Curitiba",
                "PR",
                "Rua A, 123",
                "41999998888"
        );

        filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua A, 123")
                .telefone("41999998888")
                .dataCadastro(LocalDateTime.now())
                .espacos(new ArrayList<>())
                .build();
    }

    @Test
    void shouldConvertRequestDTOToEntity() {
        // When
        Filial result = filialMapper.toEntity(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Filial Centro", result.getNome());
        assertEquals("Curitiba", result.getCidade());
        assertEquals("PR", result.getEstado());
        assertEquals("Rua A, 123", result.getEndereco());
        assertEquals("41999998888", result.getTelefone());
    }

    @Test
    void shouldConvertEntityToResponseDTO() {
        // When
        FilialResponseDTO result = filialMapper.toResponseDTO(filial);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Filial Centro", result.getNome());
        assertEquals("Curitiba", result.getCidade());
        assertEquals("PR", result.getEstado());
        assertEquals("Rua A, 123", result.getEndereco());
        assertEquals("41999998888", result.getTelefone());
        assertNotNull(result.getDataCadastro());
        assertEquals(0, result.getQuantidadeEspacos());
    }

    @Test
    void shouldHandleNullEspacosListInResponseDTO() {
        // Given
        filial.setEspacos(null);

        // When
        FilialResponseDTO result = filialMapper.toResponseDTO(filial);

        // Then
        assertEquals(0, result.getQuantidadeEspacos());
    }

    @Test
    void shouldUpdateEntityFromDTO() {
        // Given
        FilialRequestDTO updateDTO = new FilialRequestDTO(
                "Filial Atualizada",
                "São Paulo",
                "SP",
                "Rua B, 456",
                "11988887777"
        );

        // When
        filialMapper.updateEntityFromDTO(filial, updateDTO);

        // Then
        assertEquals("Filial Atualizada", filial.getNome());
        assertEquals("São Paulo", filial.getCidade());
        assertEquals("SP", filial.getEstado());
        assertEquals("Rua B, 456", filial.getEndereco());
        assertEquals("11988887777", filial.getTelefone());
    }

    @Test
    void shouldPreserveIdAndDateWhenUpdating() {
        // Given
        Integer originalId = filial.getId();
        LocalDateTime originalDate = filial.getDataCadastro();
        FilialRequestDTO updateDTO = new FilialRequestDTO(
                "Filial Atualizada",
                "São Paulo",
                "SP",
                "Rua B, 456",
                "11988887777"
        );

        // When
        filialMapper.updateEntityFromDTO(filial, updateDTO);

        // Then
        assertEquals(originalId, filial.getId());
        assertEquals(originalDate, filial.getDataCadastro());
    }
}

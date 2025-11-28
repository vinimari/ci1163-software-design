package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Filial;
import com.seucantinho.api.dto.filial.FilialRequestDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.mapper.FilialMapper;
import com.seucantinho.api.repository.FilialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilialServiceTest {

    @Mock
    private FilialRepository filialRepository;

    @Mock
    private FilialMapper filialMapper;

    @InjectMocks
    private FilialService filialService;

    private Filial filial;
    private FilialRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        filial = Filial.builder()
                .id(1)
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua Teste, 123")
                .telefone("41999999999")
                .build();

        requestDTO = FilialRequestDTO.builder()
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua Teste, 123")
                .telefone("41999999999")
                .build();
    }

    @Test
    void shouldFindAllFiliais() {
        // Given
        // Given
        when(filialRepository.findAll()).thenReturn(Arrays.asList(filial));
        when(filialMapper.toResponseDTO(any(Filial.class))).thenReturn(
                FilialResponseDTO.builder()
                        .id(filial.getId())
                        .nome(filial.getNome())
                        .build()
        );
        // When

        // Then
        // When
        List<FilialResponseDTO> result = filialService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Filial Teste");
        // Given
        verify(filialRepository, times(1)).findAll();
    }

    @Test
    void shouldFindFilialById() {
        // Given
        when(filialRepository.findById(1)).thenReturn(Optional.of(filial));
        // When
        when(filialMapper.toResponseDTO(any(Filial.class))).thenReturn(
        // Then
                FilialResponseDTO.builder()
                        .id(filial.getId())
                        .nome(filial.getNome())
                        .build()
        );

        // When
        // Given
        FilialResponseDTO result = filialService.findById(1);
        // When & Then

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getNome()).isEqualTo("Filial Teste");
        verify(filialRepository, times(1)).findById(1);
        // Given
    }

    @Test
    void shouldThrowExceptionWhenFilialNotFound() {
        // Given
        when(filialRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        // When
        assertThatThrownBy(() -> filialService.findById(999))
        // Then
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Filial não encontrada com ID: 999");
    }

    @Test
    void shouldCreateFilial() {
        // Given
        // Given
        when(filialMapper.toEntity(any(FilialRequestDTO.class))).thenReturn(filial);
        when(filialRepository.save(any(Filial.class))).thenReturn(filial);
        when(filialMapper.toResponseDTO(any(Filial.class))).thenReturn(
                FilialResponseDTO.builder()
                        .id(filial.getId())
                        .nome(filial.getNome())
                        .build()
        );

        // When
        FilialResponseDTO result = filialService.create(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Filial Teste");
        verify(filialRepository, times(1)).save(any(Filial.class));
        // When
    }
        // Then

    @Test
    void shouldUpdateFilial() {
        // Given
        when(filialRepository.findById(1)).thenReturn(Optional.of(filial));
        doNothing().when(filialMapper).updateEntityFromDTO(any(Filial.class), any(FilialRequestDTO.class));
        // Given
        when(filialRepository.save(any(Filial.class))).thenReturn(filial);
        when(filialMapper.toResponseDTO(any(Filial.class))).thenReturn(
        // When
                FilialResponseDTO.builder()
        // Then
                        .id(filial.getId())
                        .nome(filial.getNome())
                        .build()
        );

        // Given
        FilialRequestDTO updateDTO = FilialRequestDTO.builder()
        // When & Then
                .nome("Filial Atualizada")
                .cidade("São Paulo")
                .estado("SP")
                .endereco("Rua Nova, 456")
                .telefone("11988888888")
                .build();

        // When
        FilialResponseDTO result = filialService.update(1, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(filialRepository, times(1)).findById(1);
        verify(filialRepository, times(1)).save(any(Filial.class));
    }

    @Test
    void shouldDeleteFilial() {
        // Given
        when(filialRepository.existsById(1)).thenReturn(true);
        doNothing().when(filialRepository).deleteById(1);

        // When
        filialService.delete(1);

        // Then
        verify(filialRepository, times(1)).existsById(1);
        verify(filialRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentFilial() {
        // Given
        when(filialRepository.existsById(999)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> filialService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Filial não encontrada com ID: 999");
    }
}

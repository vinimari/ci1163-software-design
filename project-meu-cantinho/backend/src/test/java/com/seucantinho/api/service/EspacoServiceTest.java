package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Filial;
import com.seucantinho.api.dto.espaco.EspacoRequestDTO;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.mapper.EspacoMapper;
import com.seucantinho.api.repository.EspacoRepository;
import com.seucantinho.api.repository.FilialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspacoServiceTest {

    @Mock
    private EspacoRepository espacoRepository;

    @Mock
    private FilialRepository filialRepository;

    @Mock
    private EspacoMapper espacoMapper;

    @InjectMocks
    private EspacoService espacoService;

    private Espaco espaco;
    private Filial filial;
    private EspacoRequestDTO requestDTO;
    private EspacoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        espaco = Espaco.builder()
                .id(1)
                .nome("Sala de Reunião A")
                .descricao("Sala com capacidade para 20 pessoas")
                .capacidade(20)
                .precoDiaria(new BigDecimal("100.00"))
                .ativo(true)
                .filial(filial)
                .build();

        requestDTO = EspacoRequestDTO.builder()
                .nome("Sala de Reunião A")
                .descricao("Sala com capacidade para 20 pessoas")
                .capacidade(20)
                .precoDiaria(new BigDecimal("100.00"))
                .ativo(true)
                .filialId(1)
                .build();

        responseDTO = EspacoResponseDTO.builder()
                .id(1)
                .nome("Sala de Reunião A")
                .descricao("Sala com capacidade para 20 pessoas")
                .capacidade(20)
                .precoDiaria(new BigDecimal("100.00"))
                .ativo(true)
                .build();
    }

    @Test
    void shouldFindAllEspacos() {
        // Given
        Espaco espaco2 = Espaco.builder()
                .id(2)
                .nome("Sala de Reunião B")
                .build();

        EspacoResponseDTO responseDTO2 = EspacoResponseDTO.builder()
                .id(2)
                .nome("Sala de Reunião B")
                .build();

        when(espacoRepository.findAll()).thenReturn(Arrays.asList(espaco, espaco2));
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);
        when(espacoMapper.toResponseDTO(espaco2)).thenReturn(responseDTO2);

        // When
        List<EspacoResponseDTO> result = espacoService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNome()).isEqualTo("Sala de Reunião A");
        assertThat(result.get(1).getNome()).isEqualTo("Sala de Reunião B");
        verify(espacoRepository, times(1)).findAll();
    }

    @Test
    void shouldFindAllEspacos_WhenEmpty() {
        // Given
        when(espacoRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<EspacoResponseDTO> result = espacoService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(espacoRepository, times(1)).findAll();
    }

    @Test
    void shouldFindByFilialId() {
        // Given
        when(espacoRepository.findByFilialId(1)).thenReturn(Arrays.asList(espaco));
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // When
        List<EspacoResponseDTO> result = espacoService.findByFilialId(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Sala de Reunião A");
        verify(espacoRepository, times(1)).findByFilialId(1);
    }

    @Test
    void shouldFindByFilialId_WhenNoEspacos() {
        // Given
        when(espacoRepository.findByFilialId(999)).thenReturn(Collections.emptyList());

        // When
        List<EspacoResponseDTO> result = espacoService.findByFilialId(999);

        // Then
        assertThat(result).isEmpty();
        verify(espacoRepository, times(1)).findByFilialId(999);
    }

    @Test
    void shouldFindAtivos() {
        // Given
        when(espacoRepository.findByAtivoTrue()).thenReturn(Arrays.asList(espaco));
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // When
        List<EspacoResponseDTO> result = espacoService.findAtivos();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAtivo()).isTrue();
        verify(espacoRepository, times(1)).findByAtivoTrue();
    }

    @Test
    void shouldFindAtivos_WhenNone() {
        // Given
        when(espacoRepository.findByAtivoTrue()).thenReturn(Collections.emptyList());

        // When
        List<EspacoResponseDTO> result = espacoService.findAtivos();

        // Then
        assertThat(result).isEmpty();
        verify(espacoRepository, times(1)).findByAtivoTrue();
    }

    @Test
    void shouldFindDisponiveisPorData() {
        // Given
        LocalDate data = LocalDate.now().plusDays(7);
        Integer capacidadeMinima = 10;

        when(espacoRepository.findEspacosDisponiveisPorData(data, capacidadeMinima))
                .thenReturn(Arrays.asList(espaco));
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // When
        List<EspacoResponseDTO> result = espacoService.findDisponiveisPorData(data, capacidadeMinima);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCapacidade()).isGreaterThanOrEqualTo(capacidadeMinima);
        verify(espacoRepository, times(1)).findEspacosDisponiveisPorData(data, capacidadeMinima);
    }

    @Test
    void shouldFindDisponiveisPorData_WhenNoneAvailable() {
        // Given
        LocalDate data = LocalDate.now().plusDays(7);
        Integer capacidadeMinima = 100;

        when(espacoRepository.findEspacosDisponiveisPorData(data, capacidadeMinima))
                .thenReturn(Collections.emptyList());

        // When
        List<EspacoResponseDTO> result = espacoService.findDisponiveisPorData(data, capacidadeMinima);

        // Then
        assertThat(result).isEmpty();
        verify(espacoRepository, times(1)).findEspacosDisponiveisPorData(data, capacidadeMinima);
    }

    @Test
    void shouldFindEspacoById() {
        // Given
        when(espacoRepository.findByIdWithFilial(1)).thenReturn(Optional.of(espaco));
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // When
        EspacoResponseDTO result = espacoService.findById(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getNome()).isEqualTo("Sala de Reunião A");
        verify(espacoRepository, times(1)).findByIdWithFilial(1);
    }

    @Test
    void shouldThrowExceptionWhenEspacoNotFound() {
        // Given
        when(espacoRepository.findByIdWithFilial(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> espacoService.findById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Espaço não encontrado com ID: 999");

        verify(espacoRepository, times(1)).findByIdWithFilial(999);
    }

    @Test
    void shouldCreateEspaco() {
        // Given
        when(filialRepository.findById(1)).thenReturn(Optional.of(filial));
        when(espacoMapper.toEntity(requestDTO, filial)).thenReturn(espaco);
        when(espacoRepository.save(espaco)).thenReturn(espaco);
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // When
        EspacoResponseDTO result = espacoService.create(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Sala de Reunião A");
        verify(filialRepository, times(1)).findById(1);
        verify(espacoMapper, times(1)).toEntity(requestDTO, filial);
        verify(espacoRepository, times(1)).save(espaco);
    }

    @Test
    void shouldThrowExceptionWhenCreateWithInvalidFilial() {
        // Given
        when(filialRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        EspacoRequestDTO invalidDTO = EspacoRequestDTO.builder()
                .nome(requestDTO.getNome())
                .descricao(requestDTO.getDescricao())
                .capacidade(requestDTO.getCapacidade())
                .precoDiaria(requestDTO.getPrecoDiaria())
                .ativo(requestDTO.getAtivo())
                .filialId(999)
                .build();

        assertThatThrownBy(() -> espacoService.create(invalidDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Filial não encontrada com ID: 999");

        verify(filialRepository, times(1)).findById(999);
        verify(espacoRepository, never()).save(any());
    }

    @Test
    void shouldUpdateEspaco() {
        // Given
        EspacoRequestDTO updateDTO = EspacoRequestDTO.builder()
                .nome("Sala de Reunião A - Atualizada")
                .descricao("Sala renovada")
                .capacidade(25)
                .precoDiaria(new BigDecimal("120.00"))
                .ativo(true)
                .filialId(1)
                .build();

        when(espacoRepository.findById(1)).thenReturn(Optional.of(espaco));
        when(filialRepository.findById(1)).thenReturn(Optional.of(filial));
        doNothing().when(espacoMapper).updateEntityFromDTO(espaco, updateDTO, filial);
        when(espacoRepository.save(espaco)).thenReturn(espaco);
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // When
        EspacoResponseDTO result = espacoService.update(1, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(espacoRepository, times(1)).findById(1);
        verify(filialRepository, times(1)).findById(1);
        verify(espacoMapper, times(1)).updateEntityFromDTO(espaco, updateDTO, filial);
        verify(espacoRepository, times(1)).save(espaco);
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentEspaco() {
        // Given
        when(espacoRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> espacoService.update(999, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Espaço não encontrado com ID: 999");

        verify(espacoRepository, times(1)).findById(999);
        verify(espacoRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdateWithInvalidFilial() {
        // Given
        when(espacoRepository.findById(1)).thenReturn(Optional.of(espaco));
        when(filialRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        EspacoRequestDTO invalidUpdateDTO = EspacoRequestDTO.builder()
                .nome(requestDTO.getNome())
                .descricao(requestDTO.getDescricao())
                .capacidade(requestDTO.getCapacidade())
                .precoDiaria(requestDTO.getPrecoDiaria())
                .ativo(requestDTO.getAtivo())
                .filialId(999)
                .build();

        assertThatThrownBy(() -> espacoService.update(1, invalidUpdateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Filial não encontrada com ID: 999");

        verify(espacoRepository, times(1)).findById(1);
        verify(filialRepository, times(1)).findById(999);
        verify(espacoRepository, never()).save(any());
    }

    @Test
    void shouldDeleteEspaco() {
        // Given
        when(espacoRepository.existsById(1)).thenReturn(true);
        doNothing().when(espacoRepository).deleteById(1);

        // When
        espacoService.delete(1);

        // Then
        verify(espacoRepository, times(1)).existsById(1);
        verify(espacoRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentEspaco() {
        // Given
        when(espacoRepository.existsById(999)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> espacoService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Espaço não encontrado com ID: 999");

        verify(espacoRepository, times(1)).existsById(999);
        verify(espacoRepository, never()).deleteById(any());
    }
}

package com.seucantinho.api.feature.espaco.application.service;

import com.seucantinho.api.feature.espaco.application.dto.EspacoRequestDTO;
import com.seucantinho.api.feature.espaco.application.dto.EspacoResponseDTO;
import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.port.out.EspacoRepositoryPort;
import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.espaco.infrastructure.mapper.EspacoMapper;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.domain.port.out.FilialRepositoryPort;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EspacoService")
class EspacoServiceTest {

    @Mock
    private EspacoRepositoryPort espacoRepositoryPort;

    @Mock
    private FilialRepositoryPort filialRepositoryPort;

    @Mock
    private EspacoMapper espacoMapper;

    @InjectMocks
    private EspacoService espacoService;

    private Filial filial;
    private Espaco espaco;
    private EspacoRequestDTO requestDTO;
    private EspacoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        filial = criarFilial();
        espaco = criarEspaco();
        requestDTO = criarRequestDTO();
        responseDTO = criarResponseDTO();
    }

    @Test
    @DisplayName("Deve retornar todos os espaços")
    void deveRetornarTodosOsEspacos() {
        // Arrange
        List<Espaco> espacos = Arrays.asList(espaco, criarEspaco());
        when(espacoRepositoryPort.findAll()).thenReturn(espacos);
        when(espacoMapper.toResponseDTO(any(Espaco.class))).thenReturn(responseDTO);

        // Act
        List<EspacoResponseDTO> resultado = espacoService.findAll();

        // Assert
        assertThat(resultado).hasSize(2);
        verify(espacoRepositoryPort).findAll();
        verify(espacoMapper, times(2)).toResponseDTO(any(Espaco.class));
    }

    @Test
    @DisplayName("Deve retornar espaços por filial ID")
    void deveRetornarEspacosPorFilialId() {
        // Arrange
        Integer filialId = 1;
        List<Espaco> espacos = Arrays.asList(espaco);
        when(espacoRepositoryPort.findByFilialId(filialId)).thenReturn(espacos);
        when(espacoMapper.toResponseDTO(any(Espaco.class))).thenReturn(responseDTO);

        // Act
        List<EspacoResponseDTO> resultado = espacoService.findByFilialId(filialId);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(espacoRepositoryPort).findByFilialId(filialId);
        verify(espacoMapper).toResponseDTO(any(Espaco.class));
    }

    @Test
    @DisplayName("Deve retornar apenas espaços ativos")
    void deveRetornarApenasEspacosAtivos() {
        // Arrange
        List<Espaco> espacosAtivos = Arrays.asList(espaco);
        when(espacoRepositoryPort.findByAtivoTrue()).thenReturn(espacosAtivos);
        when(espacoMapper.toResponseDTO(any(Espaco.class))).thenReturn(responseDTO);

        // Act
        List<EspacoResponseDTO> resultado = espacoService.findAtivos();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(espacoRepositoryPort).findByAtivoTrue();
        verify(espacoMapper).toResponseDTO(any(Espaco.class));
    }

    @Test
    @DisplayName("Deve retornar espaços disponíveis por data e capacidade")
    void deveRetornarEspacosDisponiveisPorDataECapacidade() {
        // Arrange
        LocalDate data = LocalDate.now().plusDays(10);
        Integer capacidadeMinima = 50;
        List<Espaco> espacosDisponiveis = Arrays.asList(espaco);
        when(espacoRepositoryPort.findEspacosDisponiveisPorData(data, capacidadeMinima))
                .thenReturn(espacosDisponiveis);
        when(espacoMapper.toResponseDTO(any(Espaco.class))).thenReturn(responseDTO);

        // Act
        List<EspacoResponseDTO> resultado = espacoService.findDisponiveisPorData(data, capacidadeMinima);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(espacoRepositoryPort).findEspacosDisponiveisPorData(data, capacidadeMinima);
        verify(espacoMapper).toResponseDTO(any(Espaco.class));
    }

    @Test
    @DisplayName("Deve retornar espaço por ID")
    void deveRetornarEspacoPorId() {
        // Arrange
        Integer id = 1;
        when(espacoRepositoryPort.findByIdWithFilial(id)).thenReturn(Optional.of(espaco));
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // Act
        EspacoResponseDTO resultado = espacoService.findById(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(espacoRepositoryPort).findByIdWithFilial(id);
        verify(espacoMapper).toResponseDTO(espaco);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar espaço por ID inexistente")
    void deveLancarExcecaoAoBuscarEspacoPorIdInexistente() {
        // Arrange
        Integer id = 999;
        when(espacoRepositoryPort.findByIdWithFilial(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> espacoService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Espaço não encontrado com ID: " + id);
        verify(espacoRepositoryPort).findByIdWithFilial(id);
        verify(espacoMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve criar espaço com sucesso")
    void deveCriarEspacoComSucesso() {
        // Arrange
        when(filialRepositoryPort.findById(requestDTO.getFilialId())).thenReturn(Optional.of(filial));
        when(espacoMapper.toEntity(requestDTO, filial)).thenReturn(espaco);
        when(espacoRepositoryPort.save(espaco)).thenReturn(espaco);
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // Act
        EspacoResponseDTO resultado = espacoService.create(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(filialRepositoryPort).findById(requestDTO.getFilialId());
        verify(espacoMapper).toEntity(requestDTO, filial);
        verify(espacoRepositoryPort).save(espaco);
        verify(espacoMapper).toResponseDTO(espaco);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar espaço com filial inexistente")
    void deveLancarExcecaoAoCriarEspacoComFilialInexistente() {
        // Arrange
        when(filialRepositoryPort.findById(requestDTO.getFilialId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> espacoService.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Filial não encontrada com ID: " + requestDTO.getFilialId());
        verify(filialRepositoryPort).findById(requestDTO.getFilialId());
        verify(espacoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar espaço com sucesso")
    void deveAtualizarEspacoComSucesso() {
        // Arrange
        Integer id = 1;
        when(espacoRepositoryPort.findById(id)).thenReturn(Optional.of(espaco));
        when(filialRepositoryPort.findById(requestDTO.getFilialId())).thenReturn(Optional.of(filial));
        doNothing().when(espacoMapper).updateEntityFromDTO(espaco, requestDTO, filial);
        when(espacoRepositoryPort.save(espaco)).thenReturn(espaco);
        when(espacoMapper.toResponseDTO(espaco)).thenReturn(responseDTO);

        // Act
        EspacoResponseDTO resultado = espacoService.update(id, requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(espacoRepositoryPort).findById(id);
        verify(filialRepositoryPort).findById(requestDTO.getFilialId());
        verify(espacoMapper).updateEntityFromDTO(espaco, requestDTO, filial);
        verify(espacoRepositoryPort).save(espaco);
        verify(espacoMapper).toResponseDTO(espaco);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar espaço inexistente")
    void deveLancarExcecaoAoAtualizarEspacoInexistente() {
        // Arrange
        Integer id = 999;
        when(espacoRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> espacoService.update(id, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Espaço não encontrado com ID: " + id);
        verify(espacoRepositoryPort).findById(id);
        verify(espacoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar espaço com sucesso")
    void deveDeletarEspacoComSucesso() {
        // Arrange
        Integer id = 1;
        when(espacoRepositoryPort.findById(id)).thenReturn(Optional.of(espaco));
        doNothing().when(espacoRepositoryPort).deleteById(id);

        // Act
        espacoService.delete(id);

        // Assert
        verify(espacoRepositoryPort).findById(id);
        verify(espacoRepositoryPort).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar espaço inexistente")
    void deveLancarExcecaoAoDeletarEspacoInexistente() {
        // Arrange
        Integer id = 999;
        when(espacoRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> espacoService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Espaço não encontrado com ID: " + id);
        verify(espacoRepositoryPort).findById(id);
        verify(espacoRepositoryPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há espaços ativos")
    void deveRetornarListaVaziaQuandoNaoHaEspacosAtivos() {
        // Arrange
        when(espacoRepositoryPort.findByAtivoTrue()).thenReturn(Arrays.asList());

        // Act
        List<EspacoResponseDTO> resultado = espacoService.findAtivos();

        // Assert
        assertThat(resultado).isEmpty();
        verify(espacoRepositoryPort).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há espaços disponíveis")
    void deveRetornarListaVaziaQuandoNaoHaEspacosDisponiveis() {
        // Arrange
        LocalDate data = LocalDate.now().plusDays(10);
        Integer capacidadeMinima = 100;
        when(espacoRepositoryPort.findEspacosDisponiveisPorData(data, capacidadeMinima))
                .thenReturn(Arrays.asList());

        // Act
        List<EspacoResponseDTO> resultado = espacoService.findDisponiveisPorData(data, capacidadeMinima);

        // Assert
        assertThat(resultado).isEmpty();
        verify(espacoRepositoryPort).findEspacosDisponiveisPorData(data, capacidadeMinima);
    }

    // Métodos auxiliares
    private Filial criarFilial() {
        return Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();
    }

    private Espaco criarEspaco() {
        return Espaco.builder()
                .id(1)
                .nome("Salão de Eventos")
                .descricao("Espaço amplo")
                .capacidade(Capacidade.of(50))
                .precoDiaria(ValorMonetario.of("300.00"))
                .filial(filial)
                .ativo(true)
                .build();
    }

    private EspacoRequestDTO criarRequestDTO() {
        return EspacoRequestDTO.builder()
                .nome("Salão de Eventos")
                .descricao("Espaço amplo")
                .capacidade(50)
                .precoDiaria(new BigDecimal("300.00"))
                .filialId(1)
                .ativo(true)
                .build();
    }

    private EspacoResponseDTO criarResponseDTO() {
        FilialResponseDTO filialDTO = FilialResponseDTO.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        return EspacoResponseDTO.builder()
                .id(1)
                .nome("Salão de Eventos")
                .descricao("Espaço amplo")
                .capacidade(50)
                .precoDiaria(new BigDecimal("300.00"))
                .filial(filialDTO)
                .ativo(true)
                .build();
    }
}

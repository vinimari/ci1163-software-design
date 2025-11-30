package com.seucantinho.api.feature.filial.application.service;

import com.seucantinho.api.feature.filial.application.dto.FilialRequestDTO;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.domain.port.out.FilialRepositoryPort;
import com.seucantinho.api.feature.filial.infrastructure.mapper.FilialMapper;
import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.funcionario.infrastructure.persistence.FuncionarioRepository;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do FilialService")
class FilialServiceTest {

    @Mock
    private FilialRepositoryPort filialRepositoryPort;

    @Mock
    private FilialMapper filialMapper;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FilialService filialService;

    private Filial filial;
    private FilialRequestDTO requestDTO;
    private FilialResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        filial = criarFilial();
        requestDTO = criarRequestDTO();
        responseDTO = criarResponseDTO();
    }

    @Test
    @DisplayName("Deve retornar todas as filiais")
    void deveRetornarTodasAsFiliais() {
        // Arrange
        List<Filial> filiais = Arrays.asList(filial);
        when(filialRepositoryPort.findAll()).thenReturn(filiais);
        when(filialMapper.toResponseDTO(any(Filial.class))).thenReturn(responseDTO);

        // Act
        List<FilialResponseDTO> resultado = filialService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(filialRepositoryPort).findAll();
        verify(filialMapper).toResponseDTO(any(Filial.class));
    }

    @Test
    @DisplayName("Deve retornar filial por ID")
    void deveRetornarFilialPorId() {
        // Arrange
        Integer id = 1;
        when(filialRepositoryPort.findById(id)).thenReturn(Optional.of(filial));
        when(filialMapper.toResponseDTO(filial)).thenReturn(responseDTO);

        // Act
        FilialResponseDTO resultado = filialService.findById(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(filialRepositoryPort).findById(id);
        verify(filialMapper).toResponseDTO(filial);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar filial por ID inexistente")
    void deveLancarExcecaoAoBuscarFilialPorIdInexistente() {
        // Arrange
        Integer id = 999;
        when(filialRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> filialService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Filial não encontrada com ID: " + id);
        verify(filialRepositoryPort).findById(id);
        verify(filialMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve criar filial com sucesso")
    void deveCriarFilialComSucesso() {
        // Arrange
        when(filialMapper.toEntity(requestDTO)).thenReturn(filial);
        when(filialRepositoryPort.save(filial)).thenReturn(filial);
        when(filialMapper.toResponseDTO(filial)).thenReturn(responseDTO);

        // Act
        FilialResponseDTO resultado = filialService.create(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(filialMapper).toEntity(requestDTO);
        verify(filialRepositoryPort).save(filial);
        verify(filialMapper).toResponseDTO(filial);
    }

    @Test
    @DisplayName("Deve atualizar filial com sucesso")
    void deveAtualizarFilialComSucesso() {
        // Arrange
        Integer id = 1;
        when(filialRepositoryPort.findById(id)).thenReturn(Optional.of(filial));
        doNothing().when(filialMapper).updateEntityFromDTO(filial, requestDTO);
        when(filialRepositoryPort.save(filial)).thenReturn(filial);
        when(filialMapper.toResponseDTO(filial)).thenReturn(responseDTO);

        // Act
        FilialResponseDTO resultado = filialService.update(id, requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(filialRepositoryPort).findById(id);
        verify(filialMapper).updateEntityFromDTO(filial, requestDTO);
        verify(filialRepositoryPort).save(filial);
        verify(filialMapper).toResponseDTO(filial);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar filial inexistente")
    void deveLancarExcecaoAoAtualizarFilialInexistente() {
        // Arrange
        Integer id = 999;
        when(filialRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> filialService.update(id, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Filial não encontrada com ID: " + id);
        verify(filialRepositoryPort).findById(id);
        verify(filialRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar filial com sucesso")
    void deveDeletarFilialComSucesso() {
        // Arrange
        Integer id = 1;
        when(filialRepositoryPort.findById(id)).thenReturn(Optional.of(filial));
        when(funcionarioRepository.findByFilialId(id)).thenReturn(Collections.emptyList());
        doNothing().when(filialRepositoryPort).deleteById(id);

        // Act
        filialService.delete(id);

        // Assert
        verify(filialRepositoryPort).findById(id);
        verify(funcionarioRepository).findByFilialId(id);
        verify(filialRepositoryPort).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar filial inexistente")
    void deveLancarExcecaoAoDeletarFilialInexistente() {
        // Arrange
        Integer id = 999;
        when(filialRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> filialService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Filial não encontrada com ID: " + id);
        verify(filialRepositoryPort).findById(id);
        verify(filialRepositoryPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar filial com funcionários associados")
    void deveLancarExcecaoAoDeletarFilialComFuncionarios() {
        // Arrange
        Integer id = 1;
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1);
        funcionario.setNome("João Silva");
        funcionario.setFilial(filial);

        when(filialRepositoryPort.findById(id)).thenReturn(Optional.of(filial));
        when(funcionarioRepository.findByFilialId(id)).thenReturn(Arrays.asList(funcionario));

        // Act & Assert
        assertThatThrownBy(() -> filialService.delete(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não é possível excluir a filial pois existem funcionários associados");
        verify(filialRepositoryPort).findById(id);
        verify(funcionarioRepository).findByFilialId(id);
        verify(filialRepositoryPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há filiais")
    void deveRetornarListaVaziaQuandoNaoHaFiliais() {
        // Arrange
        when(filialRepositoryPort.findAll()).thenReturn(Arrays.asList());

        // Act
        List<FilialResponseDTO> resultado = filialService.findAll();

        // Assert
        assertThat(resultado).isEmpty();
        verify(filialRepositoryPort).findAll();
    }

    // Métodos auxiliares
    private Filial criarFilial() {
        return Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua XV de Novembro, 1000")
                .telefone("(41) 3333-4444")
                .build();
    }

    private FilialRequestDTO criarRequestDTO() {
        return FilialRequestDTO.builder()
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua XV de Novembro, 1000")
                .telefone("(41) 3333-4444")
                .build();
    }

    private FilialResponseDTO criarResponseDTO() {
        return FilialResponseDTO.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua XV de Novembro, 1000")
                .telefone("(41) 3333-4444")
                .build();
    }
}

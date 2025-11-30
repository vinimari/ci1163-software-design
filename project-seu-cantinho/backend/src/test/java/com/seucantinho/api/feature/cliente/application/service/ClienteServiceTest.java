package com.seucantinho.api.feature.cliente.application.service;

import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.cliente.domain.port.out.ClienteRepositoryPort;
import com.seucantinho.api.feature.cliente.domain.service.ClienteUniquenessService;
import com.seucantinho.api.feature.cliente.infrastructure.mapper.ClienteMapper;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private ClienteUniquenessService clienteUniquenessService;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteRequestDTO requestDTO;
    private ClienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .cpf("12345678901")
                .ativo(true)
                .build();

        requestDTO = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .cpf("12345678901")
                .build();

        responseDTO = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678901")
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve retornar todos os clientes")
    void deveRetornarTodosOsClientes() {
        // Arrange
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepositoryPort.findAll()).thenReturn(clientes);
        when(clienteMapper.toResponseDTO(any(Cliente.class))).thenReturn(responseDTO);

        // Act
        List<ClienteResponseDTO> resultado = clienteService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(clienteRepositoryPort).findAll();
        verify(clienteMapper).toResponseDTO(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar cliente por ID")
    void deveRetornarClientePorId() {
        // Arrange
        Integer id = 1;
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.findById(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(clienteRepositoryPort).findById(id);
        verify(clienteMapper).toResponseDTO(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente por ID inexistente")
    void deveLancarExcecaoAoBuscarClientePorIdInexistente() {
        // Arrange
        Integer id = 999;
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado com ID: " + id);
        verify(clienteRepositoryPort).findById(id);
        verify(clienteMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void deveCriarClienteComSucesso() {
        // Arrange
        doNothing().when(clienteUniquenessService).validarEmailUnico(requestDTO.getEmail());
        doNothing().when(clienteUniquenessService).validarCpfUnico(requestDTO.getCpf());
        when(clienteMapper.toEntity(requestDTO)).thenReturn(cliente);
        when(clienteRepositoryPort.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.create(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(clienteUniquenessService).validarEmailUnico(requestDTO.getEmail());
        verify(clienteUniquenessService).validarCpfUnico(requestDTO.getCpf());
        verify(clienteMapper).toEntity(requestDTO);
        verify(clienteRepositoryPort).save(cliente);
        verify(clienteMapper).toResponseDTO(cliente);
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        // Arrange
        Integer id = 1;
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteUniquenessService).validarEmailUnicoParaAtualizacao(requestDTO.getEmail(), id);
        doNothing().when(clienteMapper).updateEntityFromDTO(cliente, requestDTO);
        when(clienteRepositoryPort.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.update(id, requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(clienteRepositoryPort).findById(id);
        verify(clienteUniquenessService).validarEmailUnicoParaAtualizacao(requestDTO.getEmail(), id);
        verify(clienteMapper, times(2)).updateEntityFromDTO(cliente, requestDTO);
        verify(clienteRepositoryPort).save(cliente);
        verify(clienteMapper).toResponseDTO(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        // Arrange
        Integer id = 999;
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.update(id, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado com ID: " + id);
        verify(clienteRepositoryPort).findById(id);
        verify(clienteRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void deveDeletarClienteComSucesso() {
        // Arrange
        Integer id = 1;
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepositoryPort).deleteById(id);

        // Act
        clienteService.delete(id);

        // Assert
        verify(clienteRepositoryPort).findById(id);
        verify(clienteRepositoryPort).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cliente inexistente")
    void deveLancarExcecaoAoDeletarClienteInexistente() {
        // Arrange
        Integer id = 999;
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado com ID: " + id);
        verify(clienteRepositoryPort).findById(id);
        verify(clienteRepositoryPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve ativar cliente com sucesso")
    void deveAtivarClienteComSucesso() {
        // Arrange
        Integer id = 1;
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteRepositoryPort.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.toggleAtivo(id, true);

        // Assert
        assertThat(resultado).isNotNull();
        verify(clienteRepositoryPort).findById(id);
        verify(clienteRepositoryPort).save(cliente);
    }

    @Test
    @DisplayName("Deve desativar cliente com sucesso")
    void deveDesativarClienteComSucesso() {
        // Arrange
        Integer id = 1;
        cliente.setAtivo(false);
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteRepositoryPort.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.toggleAtivo(id, false);

        // Assert
        assertThat(resultado).isNotNull();
        verify(clienteRepositoryPort).findById(id);
        verify(clienteRepositoryPort).save(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao toggle ativo em cliente inexistente")
    void deveLancarExcecaoAoToggleAtivoEmClienteInexistente() {
        // Arrange
        Integer id = 999;
        when(clienteRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.toggleAtivo(id, true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado com ID: " + id);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há clientes")
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        // Arrange
        when(clienteRepositoryPort.findAll()).thenReturn(Arrays.asList());

        // Act
        List<ClienteResponseDTO> resultado = clienteService.findAll();

        // Assert
        assertThat(resultado).isEmpty();
        verify(clienteRepositoryPort).findAll();
    }
}

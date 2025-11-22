package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Cliente;
import com.seucantinho.api.dto.usuario.ClienteRequestDTO;
import com.seucantinho.api.dto.usuario.ClienteResponseDTO;
import com.seucantinho.api.exception.DuplicateResourceException;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.mapper.ClienteMapper;
import com.seucantinho.api.repository.ClienteRepository;
import com.seucantinho.api.validator.ClienteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private ClienteValidator clienteValidator;

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
                .cpf("12345678900")
                .telefone("41999999999")
                .senhaHash("$2a$10$hashedPassword")
                .ativo(true)
                .dataCadastro(LocalDateTime.now())
                .build();

        requestDTO = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("41999999999")
                .senha("senha123")
                .ativo(true)
                .build();

        responseDTO = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("41999999999")
                .ativo(true)
                .dataCadastro(LocalDateTime.now())
                .quantidadeReservas(0)
                .build();
    }

    @Test
    void shouldFindAllClientes() {
        // Given
        Cliente cliente2 = Cliente.builder()
                .id(2)
                .nome("Maria Santos")
                .email("maria@email.com")
                .build();
        
        ClienteResponseDTO responseDTO2 = ClienteResponseDTO.builder()
                .id(2)
                .nome("Maria Santos")
                .email("maria@email.com")
                .build();

        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente, cliente2));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);
        when(clienteMapper.toResponseDTO(cliente2)).thenReturn(responseDTO2);

        // When
        List<ClienteResponseDTO> result = clienteService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");
        assertThat(result.get(1).getNome()).isEqualTo("Maria Santos");
        verify(clienteRepository, times(1)).findAll();
        verify(clienteMapper, times(2)).toResponseDTO(any(Cliente.class));
    }

    @Test
    void shouldFindAllClientes_WhenEmpty() {
        // Given
        when(clienteRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ClienteResponseDTO> result = clienteService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void shouldFindClienteById() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // When
        ClienteResponseDTO result = clienteService.findById(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getEmail()).isEqualTo("joao@email.com");
        verify(clienteRepository, times(1)).findById(1);
        verify(clienteMapper, times(1)).toResponseDTO(cliente);
    }

    @Test
    void shouldThrowExceptionWhenClienteNotFound() {
        // Given
        when(clienteRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.findById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado com ID: 999");
        
        verify(clienteRepository, times(1)).findById(999);
        verify(clienteMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldCreateCliente() {
        // Given
        doNothing().when(clienteValidator).validateEmailUnique(requestDTO.getEmail());
        doNothing().when(clienteValidator).validateCpfUnique(requestDTO.getCpf());
        when(clienteMapper.toEntity(requestDTO)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // When
        ClienteResponseDTO result = clienteService.create(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getEmail()).isEqualTo("joao@email.com");
        verify(clienteValidator, times(1)).validateEmailUnique(requestDTO.getEmail());
        verify(clienteValidator, times(1)).validateCpfUnique(requestDTO.getCpf());
        verify(clienteMapper, times(1)).toEntity(requestDTO);
        verify(clienteRepository, times(1)).save(cliente);
        verify(clienteMapper, times(1)).toResponseDTO(cliente);
    }

    @Test
    void shouldThrowExceptionWhenCreateWithDuplicateEmail() {
        // Given
        doThrow(new DuplicateResourceException("Email já cadastrado: joao@email.com"))
                .when(clienteValidator).validateEmailUnique(requestDTO.getEmail());

        // When & Then
        assertThatThrownBy(() -> clienteService.create(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(clienteValidator, times(1)).validateEmailUnique(requestDTO.getEmail());
        verify(clienteValidator, never()).validateCpfUnique(any());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreateWithDuplicateCpf() {
        // Given
        doNothing().when(clienteValidator).validateEmailUnique(requestDTO.getEmail());
        doThrow(new DuplicateResourceException("CPF já cadastrado: 12345678900"))
                .when(clienteValidator).validateCpfUnique(requestDTO.getCpf());

        // When & Then
        assertThatThrownBy(() -> clienteService.create(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("CPF já cadastrado");

        verify(clienteValidator, times(1)).validateEmailUnique(requestDTO.getEmail());
        verify(clienteValidator, times(1)).validateCpfUnique(requestDTO.getCpf());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCliente() {
        // Given
        ClienteRequestDTO updateDTO = ClienteRequestDTO.builder()
                .nome("João Silva Atualizado")
                .email("joao.novo@email.com")
                .cpf("12345678900")
                .telefone("41988888888")
                .senha(null)
                .ativo(true)
                .build();

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteValidator).validateEmailUniqueForUpdate(updateDTO.getEmail(), 1);
        doNothing().when(clienteMapper).updateEntityFromDTO(cliente, updateDTO);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // When
        ClienteResponseDTO result = clienteService.update(1, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(clienteRepository, times(1)).findById(1);
        verify(clienteValidator, times(1)).validateEmailUniqueForUpdate(updateDTO.getEmail(), 1);
        verify(clienteMapper, times(1)).updateEntityFromDTO(cliente, updateDTO);
        verify(clienteRepository, times(1)).save(cliente);
        verify(clienteMapper, times(1)).toResponseDTO(cliente);
    }

    @Test
    void shouldUpdateCliente_WithPassword() {
        // Given
        ClienteRequestDTO updateDTO = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("41999999999")
                .senha("novaSenha123")
                .ativo(true)
                .build();

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteValidator).validateEmailUniqueForUpdate(updateDTO.getEmail(), 1);
        doNothing().when(clienteMapper).updateEntityFromDTO(cliente, updateDTO);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // When
        ClienteResponseDTO result = clienteService.update(1, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(clienteMapper, times(1)).updateEntityFromDTO(cliente, updateDTO);
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentCliente() {
        // Given
        when(clienteRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.update(999, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado com ID: 999");

        verify(clienteRepository, times(1)).findById(999);
        verify(clienteValidator, never()).validateEmailUniqueForUpdate(any(), any());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdateWithDuplicateEmail() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        doThrow(new DuplicateResourceException("Email já cadastrado por outro usuário"))
                .when(clienteValidator).validateEmailUniqueForUpdate(requestDTO.getEmail(), 1);

        // When & Then
        assertThatThrownBy(() -> clienteService.update(1, requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(clienteRepository, times(1)).findById(1);
        verify(clienteValidator, times(1)).validateEmailUniqueForUpdate(requestDTO.getEmail(), 1);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCliente() {
        // Given
        when(clienteRepository.existsById(1)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1);

        // When
        clienteService.delete(1);

        // Then
        verify(clienteRepository, times(1)).existsById(1);
        verify(clienteRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCliente() {
        // Given
        when(clienteRepository.existsById(999)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> clienteService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado com ID: 999");

        verify(clienteRepository, times(1)).existsById(999);
        verify(clienteRepository, never()).deleteById(any());
    }
}

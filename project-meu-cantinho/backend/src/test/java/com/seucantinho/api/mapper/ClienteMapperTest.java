package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Cliente;
import com.seucantinho.api.dto.usuario.ClienteRequestDTO;
import com.seucantinho.api.dto.usuario.ClienteResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteMapper clienteMapper;

    private ClienteRequestDTO requestDTO;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        requestDTO = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .build();

        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hashedPassword")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .dataCadastro(LocalDateTime.now())
                .reservas(new ArrayList<>())
                .build();
    }

    @Test
    void shouldConvertRequestDTOToEntity() {
        // Given
        when(passwordEncoder.encode("senha123")).thenReturn("hashedPassword");

        // When
        Cliente result = clienteMapper.toEntity(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals("hashedPassword", result.getSenhaHash());
        assertEquals("12345678900", result.getCpf());
        assertEquals("41999998888", result.getTelefone());
        assertTrue(result.getAtivo());
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void shouldSetAtivoToTrueWhenNull() {
        // Given
        requestDTO.setAtivo(null);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        // When
        Cliente result = clienteMapper.toEntity(requestDTO);

        // Then
        assertTrue(result.getAtivo());
    }

    @Test
    void shouldConvertEntityToResponseDTO() {
        // When
        ClienteResponseDTO result = clienteMapper.toResponseDTO(cliente);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("João Silva", result.getNome());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals("12345678900", result.getCpf());
        assertEquals("41999998888", result.getTelefone());
        assertTrue(result.getAtivo());
        assertNotNull(result.getDataCadastro());
        assertEquals(0, result.getQuantidadeReservas());
    }

    @Test
    void shouldHandleNullReservasListInResponseDTO() {
        // Given
        cliente.setReservas(null);

        // When
        ClienteResponseDTO result = clienteMapper.toResponseDTO(cliente);

        // Then
        assertEquals(0, result.getQuantidadeReservas());
    }

    @Test
    void shouldUpdateEntityFromDTO() {
        // Given
        when(passwordEncoder.encode("novaSenha")).thenReturn("newHashedPassword");
        requestDTO.setNome("João Updated");
        requestDTO.setEmail("joao.updated@email.com");
        requestDTO.setSenha("novaSenha");
        requestDTO.setCpf("98765432100");
        requestDTO.setTelefone("41988887777");
        requestDTO.setAtivo(false);

        // When
        clienteMapper.updateEntityFromDTO(cliente, requestDTO);

        // Then
        assertEquals("João Updated", cliente.getNome());
        assertEquals("joao.updated@email.com", cliente.getEmail());
        assertEquals("newHashedPassword", cliente.getSenhaHash());
        assertEquals("98765432100", cliente.getCpf());
        assertEquals("41988887777", cliente.getTelefone());
        assertFalse(cliente.getAtivo());
        verify(passwordEncoder).encode("novaSenha");
    }

    @Test
    void shouldNotUpdatePasswordWhenNull() {
        // Given
        String originalHash = cliente.getSenhaHash();
        requestDTO.setSenha(null);

        // When
        clienteMapper.updateEntityFromDTO(cliente, requestDTO);

        // Then
        assertEquals(originalHash, cliente.getSenhaHash());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldNotUpdatePasswordWhenEmpty() {
        // Given
        String originalHash = cliente.getSenhaHash();
        requestDTO.setSenha("");

        // When
        clienteMapper.updateEntityFromDTO(cliente, requestDTO);

        // Then
        assertEquals(originalHash, cliente.getSenhaHash());
        verify(passwordEncoder, never()).encode(anyString());
    }
}

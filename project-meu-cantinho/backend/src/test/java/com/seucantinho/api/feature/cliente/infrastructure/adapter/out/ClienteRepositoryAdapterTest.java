package com.seucantinho.api.feature.cliente.infrastructure.adapter.out;

import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.cliente.infrastructure.persistence.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ClienteRepositoryAdapter")
class ClienteRepositoryAdapterTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ClienteRepositoryAdapter(clienteRepository);
    }

    @Test
    @DisplayName("Deve salvar cliente")
    void deveSalvarCliente() {
        Cliente cliente = Cliente.builder().nome("Cliente 1").build();
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente result = adapter.save(cliente);

        assertNotNull(result);
        verify(clienteRepository).save(cliente);
    }

    @Test
    @DisplayName("Deve buscar todos os clientes")
    void deveBuscarTodosClientes() {
        Cliente cliente1 = Cliente.builder().id(1).nome("Cliente 1").build();
        Cliente cliente2 = Cliente.builder().id(2).nome("Cliente 2").build();
        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

        when(clienteRepository.findAll()).thenReturn(clientes);

        List<Cliente> result = adapter.findAll();

        assertEquals(2, result.size());
        verify(clienteRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar cliente por ID")
    void deveBuscarClientePorId() {
        Cliente cliente = Cliente.builder().id(1).nome("Cliente 1").build();
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(clienteRepository).findById(1);
    }

    @Test
    @DisplayName("Deve buscar cliente por email")
    void deveBuscarClientePorEmail() {
        Cliente cliente = Cliente.builder().email("teste@email.com").build();
        when(clienteRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = adapter.findByEmail("teste@email.com");

        assertTrue(result.isPresent());
        verify(clienteRepository).findByEmail("teste@email.com");
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF")
    void deveBuscarClientePorCpf() {
        Cliente cliente = Cliente.builder().cpf("12345678900").build();
        when(clienteRepository.findByCpf("12345678900")).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = adapter.findByCpf("12345678900");

        assertTrue(result.isPresent());
        verify(clienteRepository).findByCpf("12345678900");
    }
}


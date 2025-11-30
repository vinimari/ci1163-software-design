package com.seucantinho.api.feature.usuario.infrastructure.adapter.out;

import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.usuario.infrastructure.persistence.UsuarioRepository;
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
@DisplayName("Testes do UsuarioRepositoryAdapter")
class UsuarioRepositoryAdapterTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioRepositoryAdapter(usuarioRepository);
    }

    @Test
    @DisplayName("Deve buscar todos os usuários")
    void deveBuscarTodosUsuarios() {
        Cliente usuario1 = Cliente.builder().id(1).nome("Usuario 1").build();
        Cliente usuario2 = Cliente.builder().id(2).nome("Usuario 2").build();
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> result = adapter.findAll();

        assertEquals(2, result.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        Cliente usuario = Cliente.builder().id(1).nome("Usuario 1").build();

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(usuarioRepository).findById(1);
    }

    @Test
    @DisplayName("Deve retornar empty quando usuário não encontrado por ID")
    void deveRetornarEmptyQuandoUsuarioNaoEncontradoPorId() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Usuario> result = adapter.findById(999);

        assertFalse(result.isPresent());
        verify(usuarioRepository).findById(999);
    }

    @Test
    @DisplayName("Deve buscar usuário por email")
    void deveBuscarUsuarioPorEmail() {
        Cliente usuario = Cliente.builder().id(1).email("teste@email.com").build();

        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = adapter.findByEmail("teste@email.com");

        assertTrue(result.isPresent());
        assertEquals("teste@email.com", result.get().getEmail());
        verify(usuarioRepository).findByEmail("teste@email.com");
    }

    @Test
    @DisplayName("Deve verificar se existe usuário com email")
    void deveVerificarSeExisteUsuarioComEmail() {
        when(usuarioRepository.existsByEmail("teste@email.com")).thenReturn(true);

        boolean result = adapter.existsByEmail("teste@email.com");

        assertTrue(result);
        verify(usuarioRepository).existsByEmail("teste@email.com");
    }

    @Test
    @DisplayName("Deve verificar se existe usuário com CPF")
    void deveVerificarSeExisteUsuarioComCpf() {
        when(usuarioRepository.existsByCpf("12345678900")).thenReturn(true);

        boolean result = adapter.existsByCpf("12345678900");

        assertTrue(result);
        verify(usuarioRepository).existsByCpf("12345678900");
    }
}


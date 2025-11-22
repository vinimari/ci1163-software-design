package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void shouldCreateClienteWithBuilder() {
        Cliente cliente = Cliente.builder()
            .id(1)
            .nome("João Silva")
            .email("joao@example.com")
            .senhaHash("hashedpassword")
            .cpf("12345678901")
            .telefone("11999999999")
            .ativo(true)
            .dataCadastro(LocalDateTime.now())
            .reservas(new ArrayList<>())
            .build();

        assertNotNull(cliente);
        assertEquals(1, cliente.getId());
        assertEquals("João Silva", cliente.getNome());
        assertEquals("joao@example.com", cliente.getEmail());
        assertEquals("12345678901", cliente.getCpf());
        assertEquals(PerfilUsuarioEnum.CLIENTE, cliente.getPerfil());
    }

    @Test
    void shouldGetPerfilAsCliente() {
        Cliente cliente = new Cliente();
        assertEquals(PerfilUsuarioEnum.CLIENTE, cliente.getPerfil());
    }

    @Test
    void shouldSetAndGetReservas() {
        Cliente cliente = new Cliente();
        Reserva reserva = new Reserva();
        cliente.getReservas().add(reserva);

        assertEquals(1, cliente.getReservas().size());
        assertTrue(cliente.getReservas().contains(reserva));
    }

    @Test
    void shouldInitializeReservasListByDefault() {
        Cliente cliente = Cliente.builder().build();
        assertNotNull(cliente.getReservas());
        assertTrue(cliente.getReservas().isEmpty());
    }

    @Test
    void shouldSetAndGetAllProperties() {
        Cliente cliente = new Cliente();
        cliente.setId(10);
        cliente.setNome("Maria Souza");
        cliente.setEmail("maria@example.com");
        cliente.setSenhaHash("hash123");
        cliente.setCpf("98765432100");
        cliente.setTelefone("11888888888");
        cliente.setAtivo(false);
        LocalDateTime now = LocalDateTime.now();
        cliente.setDataCadastro(now);

        assertEquals(10, cliente.getId());
        assertEquals("Maria Souza", cliente.getNome());
        assertEquals("maria@example.com", cliente.getEmail());
        assertEquals("hash123", cliente.getSenhaHash());
        assertEquals("98765432100", cliente.getCpf());
        assertEquals("11888888888", cliente.getTelefone());
        assertFalse(cliente.getAtivo());
        assertEquals(now, cliente.getDataCadastro());
    }

    @Test
    void shouldInheritFromUsuario() {
        Cliente cliente = new Cliente();
        assertTrue(cliente instanceof Usuario);
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        Cliente cliente = new Cliente();
        assertNotNull(cliente);
        assertNull(cliente.getId());
        assertEquals(PerfilUsuarioEnum.CLIENTE, cliente.getPerfil());
    }
}

package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    // Classe concreta para testar a classe abstrata Usuario
    @lombok.experimental.SuperBuilder
    @lombok.NoArgsConstructor
    private static class UsuarioConcreto extends Usuario {
        @Override
        public PerfilUsuarioEnum getPerfil() {
            return PerfilUsuarioEnum.CLIENTE;
        }
    }

    @Test
    void shouldCreateUsuarioWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Usuario usuario = UsuarioConcreto.builder()
            .id(1)
            .nome("Test User")
            .email("test@example.com")
            .senhaHash("hashedPassword123")
            .cpf("12345678901")
            .telefone("11999999999")
            .ativo(true)
            .dataCadastro(now)
            .build();

        assertNotNull(usuario);
        assertEquals(1, usuario.getId());
        assertEquals("Test User", usuario.getNome());
        assertEquals("test@example.com", usuario.getEmail());
        assertEquals("hashedPassword123", usuario.getSenhaHash());
        assertEquals("12345678901", usuario.getCpf());
        assertEquals("11999999999", usuario.getTelefone());
        assertTrue(usuario.getAtivo());
        assertEquals(now, usuario.getDataCadastro());
    }

    @Test
    void shouldSetAndGetId() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setId(100);
        assertEquals(100, usuario.getId());
    }

    @Test
    void shouldSetAndGetNome() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setNome("João Silva");
        assertEquals("João Silva", usuario.getNome());
    }

    @Test
    void shouldSetAndGetEmail() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setEmail("joao@example.com");
        assertEquals("joao@example.com", usuario.getEmail());
    }

    @Test
    void shouldSetAndGetSenhaHash() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setSenhaHash("secureHash123");
        assertEquals("secureHash123", usuario.getSenhaHash());
    }

    @Test
    void shouldSetAndGetCpf() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setCpf("98765432100");
        assertEquals("98765432100", usuario.getCpf());
    }

    @Test
    void shouldSetAndGetTelefone() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setTelefone("11888888888");
        assertEquals("11888888888", usuario.getTelefone());
    }

    @Test
    void shouldSetAndGetAtivo() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setAtivo(false);
        assertFalse(usuario.getAtivo());
    }

    @Test
    void shouldSetAndGetDataCadastro() {
        Usuario usuario = new UsuarioConcreto();
        LocalDateTime now = LocalDateTime.now();
        usuario.setDataCadastro(now);
        assertEquals(now, usuario.getDataCadastro());
    }

    @Test
    void shouldInitializeAtivoAsTrueByDefault() {
        Usuario usuario = UsuarioConcreto.builder().build();
        assertTrue(usuario.getAtivo());
    }

    @Test
    void shouldAllowNullCpf() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setCpf(null);
        assertNull(usuario.getCpf());
    }

    @Test
    void shouldAllowNullTelefone() {
        Usuario usuario = new UsuarioConcreto();
        usuario.setTelefone(null);
        assertNull(usuario.getTelefone());
    }

    @Test
    void shouldBeAbstractClass() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(Usuario.class.getModifiers()));
    }

    @Test
    void shouldHaveAbstractGetPerfilMethod() throws NoSuchMethodException {
        var method = Usuario.class.getDeclaredMethod("getPerfil");
        assertTrue(java.lang.reflect.Modifier.isAbstract(method.getModifiers()));
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        Usuario usuario = new UsuarioConcreto();
        assertNotNull(usuario);
        assertNull(usuario.getId());
        assertNull(usuario.getNome());
    }
}

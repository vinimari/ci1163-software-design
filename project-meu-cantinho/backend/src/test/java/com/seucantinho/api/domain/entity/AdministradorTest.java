package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AdministradorTest {

    @Test
    void shouldCreateAdministradorWithNoArgsConstructor() {
        Administrador admin = new Administrador();
        assertNotNull(admin);
        assertEquals(PerfilUsuarioEnum.ADMIN, admin.getPerfil());
    }

    @Test
    void shouldGetPerfilAsAdmin() {
        Administrador admin = new Administrador();
        assertEquals(PerfilUsuarioEnum.ADMIN, admin.getPerfil());
    }

    @Test
    void shouldSetAndGetAllProperties() {
        Administrador admin = new Administrador();
        admin.setId(1);
        admin.setNome("Admin User");
        admin.setEmail("admin@example.com");
        admin.setSenhaHash("secureHash");
        admin.setCpf("11122233344");
        admin.setTelefone("11987654321");
        admin.setAtivo(true);
        LocalDateTime now = LocalDateTime.now();
        admin.setDataCadastro(now);

        assertEquals(1, admin.getId());
        assertEquals("Admin User", admin.getNome());
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("secureHash", admin.getSenhaHash());
        assertEquals("11122233344", admin.getCpf());
        assertEquals("11987654321", admin.getTelefone());
        assertTrue(admin.getAtivo());
        assertEquals(now, admin.getDataCadastro());
    }

    @Test
    void shouldInheritFromUsuario() {
        Administrador admin = new Administrador();
        assertTrue(admin instanceof Usuario);
    }

    @Test
    void shouldHaveCorrectPerfil() {
        Administrador admin = new Administrador();
        assertEquals(PerfilUsuarioEnum.ADMIN, admin.getPerfil());
        assertNotEquals(PerfilUsuarioEnum.CLIENTE, admin.getPerfil());
        assertNotEquals(PerfilUsuarioEnum.FUNCIONARIO, admin.getPerfil());
    }
}

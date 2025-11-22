package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest {

    @Test
    void shouldCreateFuncionarioWithNoArgsConstructor() {
        Funcionario funcionario = new Funcionario();
        assertNotNull(funcionario);
        assertEquals(PerfilUsuarioEnum.FUNCIONARIO, funcionario.getPerfil());
    }

    @Test
    void shouldGetPerfilAsFuncionario() {
        Funcionario funcionario = new Funcionario();
        assertEquals(PerfilUsuarioEnum.FUNCIONARIO, funcionario.getPerfil());
    }

    @Test
    void shouldSetAndGetMatricula() {
        Funcionario funcionario = new Funcionario();
        funcionario.setMatricula("MAT12345");

        assertEquals("MAT12345", funcionario.getMatricula());
    }

    @Test
    void shouldSetAndGetFilial() {
        Funcionario funcionario = new Funcionario();
        Filial filial = Filial.builder()
            .id(1)
            .nome("Filial Centro")
            .build();

        funcionario.setFilial(filial);

        assertNotNull(funcionario.getFilial());
        assertEquals(1, funcionario.getFilial().getId());
        assertEquals("Filial Centro", funcionario.getFilial().getNome());
    }

    @Test
    void shouldSetAndGetAllProperties() {
        Funcionario funcionario = new Funcionario();
        Filial filial = new Filial();

        funcionario.setId(5);
        funcionario.setNome("Carlos Silva");
        funcionario.setEmail("carlos@example.com");
        funcionario.setSenhaHash("hashpassword");
        funcionario.setCpf("12312312312");
        funcionario.setTelefone("11977777777");
        funcionario.setAtivo(true);
        funcionario.setMatricula("FUNC001");
        funcionario.setFilial(filial);
        LocalDateTime now = LocalDateTime.now();
        funcionario.setDataCadastro(now);

        assertEquals(5, funcionario.getId());
        assertEquals("Carlos Silva", funcionario.getNome());
        assertEquals("carlos@example.com", funcionario.getEmail());
        assertEquals("hashpassword", funcionario.getSenhaHash());
        assertEquals("12312312312", funcionario.getCpf());
        assertEquals("11977777777", funcionario.getTelefone());
        assertTrue(funcionario.getAtivo());
        assertEquals("FUNC001", funcionario.getMatricula());
        assertEquals(filial, funcionario.getFilial());
        assertEquals(now, funcionario.getDataCadastro());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        Filial filial = new Filial();
        Funcionario funcionario = new Funcionario("MAT999", filial);

        assertEquals("MAT999", funcionario.getMatricula());
        assertEquals(filial, funcionario.getFilial());
    }

    @Test
    void shouldInheritFromUsuario() {
        Funcionario funcionario = new Funcionario();
        assertTrue(funcionario instanceof Usuario);
    }

    @Test
    void shouldAllowNullFilial() {
        Funcionario funcionario = new Funcionario();
        funcionario.setFilial(null);
        assertNull(funcionario.getFilial());
    }
}

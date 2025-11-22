package com.seucantinho.api.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FilialTest {

    @Test
    void shouldCreateFilialWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Filial filial = Filial.builder()
            .id(1)
            .nome("Filial Centro")
            .cidade("São Paulo")
            .estado("SP")
            .endereco("Rua Principal, 123")
            .telefone("1133334444")
            .dataCadastro(now)
            .espacos(new ArrayList<>())
            .funcionarios(new ArrayList<>())
            .build();

        assertNotNull(filial);
        assertEquals(1, filial.getId());
        assertEquals("Filial Centro", filial.getNome());
        assertEquals("São Paulo", filial.getCidade());
        assertEquals("SP", filial.getEstado());
        assertEquals("Rua Principal, 123", filial.getEndereco());
        assertEquals("1133334444", filial.getTelefone());
        assertEquals(now, filial.getDataCadastro());
    }

    @Test
    void shouldSetAndGetAllProperties() {
        Filial filial = new Filial();
        LocalDateTime now = LocalDateTime.now();

        filial.setId(10);
        filial.setNome("Filial Norte");
        filial.setCidade("Curitiba");
        filial.setEstado("PR");
        filial.setEndereco("Av. Norte, 456");
        filial.setTelefone("4133335555");
        filial.setDataCadastro(now);

        assertEquals(10, filial.getId());
        assertEquals("Filial Norte", filial.getNome());
        assertEquals("Curitiba", filial.getCidade());
        assertEquals("PR", filial.getEstado());
        assertEquals("Av. Norte, 456", filial.getEndereco());
        assertEquals("4133335555", filial.getTelefone());
        assertEquals(now, filial.getDataCadastro());
    }

    @Test
    void shouldInitializeEspacosListByDefault() {
        Filial filial = Filial.builder().build();
        assertNotNull(filial.getEspacos());
        assertTrue(filial.getEspacos().isEmpty());
    }

    @Test
    void shouldInitializeFuncionariosListByDefault() {
        Filial filial = Filial.builder().build();
        assertNotNull(filial.getFuncionarios());
        assertTrue(filial.getFuncionarios().isEmpty());
    }

    @Test
    void shouldAddEspacosToFilial() {
        Filial filial = Filial.builder().build();
        Espaco espaco = new Espaco();
        filial.getEspacos().add(espaco);

        assertEquals(1, filial.getEspacos().size());
        assertTrue(filial.getEspacos().contains(espaco));
    }

    @Test
    void shouldAddFuncionariosToFilial() {
        Filial filial = Filial.builder().build();
        Funcionario funcionario = new Funcionario();
        filial.getFuncionarios().add(funcionario);

        assertEquals(1, filial.getFuncionarios().size());
        assertTrue(filial.getFuncionarios().contains(funcionario));
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        Filial filial = new Filial();
        assertNotNull(filial);
        assertNull(filial.getId());
        assertNull(filial.getNome());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Filial filial = new Filial(
            1,
            "Filial Sul",
            "Porto Alegre",
            "RS",
            "Rua Sul, 789",
            "5133336666",
            now,
            new ArrayList<>(),
            new ArrayList<>()
        );

        assertEquals(1, filial.getId());
        assertEquals("Filial Sul", filial.getNome());
        assertEquals("Porto Alegre", filial.getCidade());
        assertEquals("RS", filial.getEstado());
        assertNotNull(filial.getEspacos());
        assertNotNull(filial.getFuncionarios());
    }

    @Test
    void shouldSetDataCadastroOnCreate() throws Exception {
        Filial filial = new Filial();
        assertNull(filial.getDataCadastro());

        // Invocar o método onCreate via reflexão
        var method = Filial.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(filial);

        assertNotNull(filial.getDataCadastro());
    }
}

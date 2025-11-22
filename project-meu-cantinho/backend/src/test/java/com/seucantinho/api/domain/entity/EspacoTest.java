package com.seucantinho.api.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EspacoTest {

    @Test
    void shouldCreateEspacoWithBuilder() {
        Filial filial = new Filial();
        Espaco espaco = Espaco.builder()
            .id(1)
            .nome("Salão Principal")
            .descricao("Salão para eventos")
            .capacidade(100)
            .precoDiaria(new BigDecimal("500.00"))
            .ativo(true)
            .urlFotoPrincipal("http://example.com/foto.jpg")
            .filial(filial)
            .reservas(new ArrayList<>())
            .build();

        assertNotNull(espaco);
        assertEquals(1, espaco.getId());
        assertEquals("Salão Principal", espaco.getNome());
        assertEquals("Salão para eventos", espaco.getDescricao());
        assertEquals(100, espaco.getCapacidade());
        assertEquals(0, new BigDecimal("500.00").compareTo(espaco.getPrecoDiaria()));
        assertTrue(espaco.getAtivo());
        assertEquals("http://example.com/foto.jpg", espaco.getUrlFotoPrincipal());
        assertEquals(filial, espaco.getFilial());
    }

    @Test
    void shouldSetAndGetAllProperties() {
        Espaco espaco = new Espaco();
        Filial filial = new Filial();

        espaco.setId(5);
        espaco.setNome("Auditório");
        espaco.setDescricao("Espaço para palestras");
        espaco.setCapacidade(200);
        espaco.setPrecoDiaria(new BigDecimal("750.50"));
        espaco.setAtivo(false);
        espaco.setUrlFotoPrincipal("http://example.com/auditorio.jpg");
        espaco.setFilial(filial);

        assertEquals(5, espaco.getId());
        assertEquals("Auditório", espaco.getNome());
        assertEquals("Espaço para palestras", espaco.getDescricao());
        assertEquals(200, espaco.getCapacidade());
        assertEquals(0, new BigDecimal("750.50").compareTo(espaco.getPrecoDiaria()));
        assertFalse(espaco.getAtivo());
        assertEquals("http://example.com/auditorio.jpg", espaco.getUrlFotoPrincipal());
        assertEquals(filial, espaco.getFilial());
    }

    @Test
    void shouldInitializeReservasListByDefault() {
        Espaco espaco = Espaco.builder().build();
        assertNotNull(espaco.getReservas());
        assertTrue(espaco.getReservas().isEmpty());
    }

    @Test
    void shouldInitializeAtivoAsTrueByDefault() {
        Espaco espaco = Espaco.builder().build();
        assertTrue(espaco.getAtivo());
    }

    @Test
    void shouldAddReservasToEspaco() {
        Espaco espaco = Espaco.builder().build();
        Reserva reserva = new Reserva();
        espaco.getReservas().add(reserva);

        assertEquals(1, espaco.getReservas().size());
        assertTrue(espaco.getReservas().contains(reserva));
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        Espaco espaco = new Espaco();
        assertNotNull(espaco);
        assertNull(espaco.getId());
        assertNull(espaco.getNome());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        Filial filial = new Filial();
        Espaco espaco = new Espaco(
            1,
            "Sala VIP",
            "Sala exclusiva",
            50,
            new BigDecimal("300.00"),
            true,
            "http://example.com/vip.jpg",
            filial,
            new ArrayList<>()
        );

        assertEquals(1, espaco.getId());
        assertEquals("Sala VIP", espaco.getNome());
        assertEquals("Sala exclusiva", espaco.getDescricao());
        assertEquals(50, espaco.getCapacidade());
        assertEquals(filial, espaco.getFilial());
        assertNotNull(espaco.getReservas());
    }

    @Test
    void shouldAllowNullDescricao() {
        Espaco espaco = new Espaco();
        espaco.setDescricao(null);
        assertNull(espaco.getDescricao());
    }

    @Test
    void shouldAllowNullUrlFoto() {
        Espaco espaco = new Espaco();
        espaco.setUrlFotoPrincipal(null);
        assertNull(espaco.getUrlFotoPrincipal());
    }

    @Test
    void shouldSetAtivoToTrueOnCreateIfNull() throws Exception {
        Espaco espaco = new Espaco();
        espaco.setAtivo(null);

        // Invocar o método onCreate via reflexão
        var method = Espaco.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(espaco);

        assertTrue(espaco.getAtivo());
    }

    @Test
    void shouldNotOverrideAtivoIfAlreadySetOnCreate() throws Exception {
        Espaco espaco = new Espaco();
        espaco.setAtivo(false);

        // Invocar o método onCreate via reflexão
        var method = Espaco.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(espaco);

        assertFalse(espaco.getAtivo());
    }
}

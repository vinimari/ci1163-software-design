package com.seucantinho.api.feature.espaco.infrastructure.adapter.out;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.infrastructure.persistence.EspacoRepository;
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
@DisplayName("Testes do EspacoRepositoryAdapter")
class EspacoRepositoryAdapterTest {

    @Mock
    private EspacoRepository espacoRepository;

    private EspacoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EspacoRepositoryAdapter(espacoRepository);
    }

    @Test
    @DisplayName("Deve salvar espaço")
    void deveSalvarEspaco() {
        Espaco espaco = Espaco.builder().nome("Sala 1").build();
        when(espacoRepository.save(espaco)).thenReturn(espaco);

        Espaco result = adapter.save(espaco);

        assertNotNull(result);
        verify(espacoRepository).save(espaco);
    }

    @Test
    @DisplayName("Deve buscar todos os espaços")
    void deveBuscarTodosEspacos() {
        Espaco espaco1 = Espaco.builder().id(1).nome("Sala 1").build();
        Espaco espaco2 = Espaco.builder().id(2).nome("Sala 2").build();
        List<Espaco> espacos = Arrays.asList(espaco1, espaco2);

        when(espacoRepository.findAll()).thenReturn(espacos);

        List<Espaco> result = adapter.findAll();

        assertEquals(2, result.size());
        verify(espacoRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar espaço por ID")
    void deveBuscarEspacoPorId() {
        Espaco espaco = Espaco.builder().id(1).nome("Sala 1").build();
        when(espacoRepository.findById(1)).thenReturn(Optional.of(espaco));

        Optional<Espaco> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(espacoRepository).findById(1);
    }

    @Test
    @DisplayName("Deve buscar espaços por filial")
    void deveBuscarEspacosPorFilial() {
        Espaco espaco1 = Espaco.builder().id(1).nome("Sala 1").build();
        List<Espaco> espacos = Arrays.asList(espaco1);

        when(espacoRepository.findByFilialId(1)).thenReturn(espacos);

        List<Espaco> result = adapter.findByFilialId(1);

        assertEquals(1, result.size());
        verify(espacoRepository).findByFilialId(1);
    }
}


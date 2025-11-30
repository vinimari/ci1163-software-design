package com.seucantinho.api.feature.filial.infrastructure.adapter.out;

import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.infrastructure.persistence.FilialRepository;
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
@DisplayName("Testes do FilialRepositoryAdapter")
class FilialRepositoryAdapterTest {

    @Mock
    private FilialRepository filialRepository;

    private FilialRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FilialRepositoryAdapter(filialRepository);
    }

    @Test
    @DisplayName("Deve salvar filial")
    void deveSalvarFilial() {
        Filial filial = Filial.builder().nome("Filial 1").build();
        when(filialRepository.save(filial)).thenReturn(filial);

        Filial result = adapter.save(filial);

        assertNotNull(result);
        verify(filialRepository).save(filial);
    }

    @Test
    @DisplayName("Deve buscar todas as filiais")
    void deveBuscarTodasFiliais() {
        Filial filial1 = Filial.builder().id(1).nome("Filial 1").build();
        Filial filial2 = Filial.builder().id(2).nome("Filial 2").build();
        List<Filial> filiais = Arrays.asList(filial1, filial2);

        when(filialRepository.findAll()).thenReturn(filiais);

        List<Filial> result = adapter.findAll();

        assertEquals(2, result.size());
        verify(filialRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar filial por ID")
    void deveBuscarFilialPorId() {
        Filial filial = Filial.builder().id(1).nome("Filial 1").build();
        when(filialRepository.findById(1)).thenReturn(Optional.of(filial));

        Optional<Filial> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(filialRepository).findById(1);
    }
}


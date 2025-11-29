package com.seucantinho.api.feature.reserva.infrastructure.adapter.out;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.infrastructure.persistence.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaRepositoryAdapter")
class ReservaRepositoryAdapterTest {

    @Mock
    private ReservaRepository reservaRepository;

    private ReservaRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ReservaRepositoryAdapter(reservaRepository);
    }

    @Test
    @DisplayName("Deve salvar reserva")
    void deveSalvarReserva() {
        Reserva reserva = Reserva.builder().build();
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva result = adapter.save(reserva);

        assertNotNull(result);
        verify(reservaRepository).save(reserva);
    }

    @Test
    @DisplayName("Deve buscar todas as reservas")
    void deveBuscarTodasReservas() {
        Reserva reserva1 = Reserva.builder().id(1).build();
        Reserva reserva2 = Reserva.builder().id(2).build();
        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);

        when(reservaRepository.findAll()).thenReturn(reservas);

        List<Reserva> result = adapter.findAll();

        assertEquals(2, result.size());
        verify(reservaRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar reserva por ID")
    void deveBuscarReservaPorId() {
        Reserva reserva = Reserva.builder().id(1).build();
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        Optional<Reserva> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(reservaRepository).findById(1);
    }
}


package com.seucantinho.api.validator;

import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.exception.BusinessException;
import com.seucantinho.api.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaValidatorTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaValidator reservaValidator;

    private Espaco espaco;

    @BeforeEach
    void setUp() {
        espaco = Espaco.builder()
                .id(1)
                .nome("Sala de Reunião A")
                .ativo(true)
                .build();
    }

    @Test
    void shouldValidateEspacoAtivo_WhenEspacoIsActive() {
        // Given
        espaco.setAtivo(true);

        // When & Then
        assertThatCode(() -> reservaValidator.validateEspacoAtivo(espaco))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_WhenEspacoIsInactive() {
        // Given
        espaco.setAtivo(false);

        // When & Then
        assertThatThrownBy(() -> reservaValidator.validateEspacoAtivo(espaco))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Espaço não está ativo para reservas");
    }

    @Test
    void shouldValidateDisponibilidade_WhenNoConflict() {
        // Given
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(7);
        Integer reservaId = null;

        when(reservaRepository.existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId))
                .thenReturn(false);

        // When & Then
        assertThatCode(() -> reservaValidator.validateDisponibilidade(espacoId, dataEvento, reservaId))
                .doesNotThrowAnyException();

        verify(reservaRepository, times(1))
                .existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId);
    }

    @Test
    void shouldValidateDisponibilidade_WhenUpdatingSameReserva() {
        // Given
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(7);
        Integer reservaId = 1;

        when(reservaRepository.existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId))
                .thenReturn(false);

        // When & Then
        assertThatCode(() -> reservaValidator.validateDisponibilidade(espacoId, dataEvento, reservaId))
                .doesNotThrowAnyException();

        verify(reservaRepository, times(1))
                .existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId);
    }

    @Test
    void shouldThrowException_WhenEspacoAlreadyReserved() {
        // Given
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(7);
        Integer reservaId = null;

        when(reservaRepository.existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> reservaValidator.validateDisponibilidade(espacoId, dataEvento, reservaId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Espaço já possui reserva ativa para esta data");

        verify(reservaRepository, times(1))
                .existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId);
    }

    @Test
    void shouldThrowException_WhenUpdatingToConflictingDate() {
        // Given
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(7);
        Integer reservaId = 1;

        when(reservaRepository.existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> reservaValidator.validateDisponibilidade(espacoId, dataEvento, reservaId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Espaço já possui reserva ativa para esta data");

        verify(reservaRepository, times(1))
                .existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId);
    }
}

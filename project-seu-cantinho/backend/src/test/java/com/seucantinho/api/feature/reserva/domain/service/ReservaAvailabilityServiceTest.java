package com.seucantinho.api.feature.reserva.domain.service;

import com.seucantinho.api.feature.reserva.domain.port.out.ReservaRepositoryPort;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaAvailabilityService")
class ReservaAvailabilityServiceTest {

    @Mock
    private ReservaRepositoryPort reservaRepositoryPort;

    @InjectMocks
    private ReservaAvailabilityService reservaAvailabilityService;

    @Test
    @DisplayName("Deve validar disponibilidade quando espaço está livre")
    void deveValidarDisponibilidadeQuandoEspacoEstaLivre() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        Integer reservaId = null;
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, dataEvento, reservaId))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> reservaAvailabilityService.validarDisponibilidade(espacoId, dataEvento, reservaId))
                .doesNotThrowAnyException();
        
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espacoId, dataEvento, reservaId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando espaço já possui reserva ativa")
    void deveLancarExcecaoQuandoEspacoJaPossuiReservaAtiva() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        Integer reservaId = null;
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, dataEvento, reservaId))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> reservaAvailabilityService.validarDisponibilidade(espacoId, dataEvento, reservaId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Espaço já possui reserva ativa para esta data");
        
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espacoId, dataEvento, reservaId);
    }

    @Test
    @DisplayName("Deve validar disponibilidade para atualização da mesma reserva")
    void deveValidarDisponibilidadeParaAtualizacaoDaMesmaReserva() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        Integer reservaId = 1; // Mesma reserva sendo atualizada
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, dataEvento, reservaId))
                .thenReturn(false); // Não há conflito porque ignora a própria reserva

        // Act & Assert
        assertThatCode(() -> reservaAvailabilityService.validarDisponibilidade(espacoId, dataEvento, reservaId))
                .doesNotThrowAnyException();
        
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espacoId, dataEvento, reservaId);
    }

    @Test
    @DisplayName("Deve validar disponibilidade para diferentes datas no mesmo espaço")
    void deveValidarDisponibilidadeParaDiferentesDatasMesmoEspaco() {
        // Arrange
        Integer espacoId = 1;
        LocalDate data1 = LocalDate.now().plusDays(10);
        LocalDate data2 = LocalDate.now().plusDays(11);
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, data1, null))
                .thenReturn(false);
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, data2, null))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> {
            reservaAvailabilityService.validarDisponibilidade(espacoId, data1, null);
            reservaAvailabilityService.validarDisponibilidade(espacoId, data2, null);
        }).doesNotThrowAnyException();
        
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espacoId, data1, null);
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espacoId, data2, null);
    }

    @Test
    @DisplayName("Deve validar disponibilidade para diferentes espaços na mesma data")
    void deveValidarDisponibilidadeParaDiferentesEspacosMesmaData() {
        // Arrange
        Integer espaco1Id = 1;
        Integer espaco2Id = 2;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espaco1Id, dataEvento, null))
                .thenReturn(false);
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espaco2Id, dataEvento, null))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> {
            reservaAvailabilityService.validarDisponibilidade(espaco1Id, dataEvento, null);
            reservaAvailabilityService.validarDisponibilidade(espaco2Id, dataEvento, null);
        }).doesNotThrowAnyException();
        
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espaco1Id, dataEvento, null);
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espaco2Id, dataEvento, null);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reservar data já ocupada por outra reserva")
    void deveLancarExcecaoAoTentarReservarDataJaOcupadaPorOutraReserva() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        Integer reservaId = 2; // Tentando criar/atualizar reserva 2
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, dataEvento, reservaId))
                .thenReturn(true); // Já existe outra reserva ativa (reserva 1)

        // Act & Assert
        assertThatThrownBy(() -> reservaAvailabilityService.validarDisponibilidade(espacoId, dataEvento, reservaId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Espaço já possui reserva ativa para esta data");
    }

    @Test
    @DisplayName("Deve validar disponibilidade com reservaId nulo para nova reserva")
    void deveValidarDisponibilidadeComReservaIdNuloParaNovaReserva() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, dataEvento, null))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> reservaAvailabilityService.validarDisponibilidade(espacoId, dataEvento, null))
                .doesNotThrowAnyException();
        
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espacoId, dataEvento, null);
    }

    @Test
    @DisplayName("Deve verificar disponibilidade múltiplas vezes para o mesmo espaço e data")
    void deveVerificarDisponibilidadeMultiplasVezesParaMesmoEspacoEData() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, dataEvento, null))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> {
            reservaAvailabilityService.validarDisponibilidade(espacoId, dataEvento, null);
            reservaAvailabilityService.validarDisponibilidade(espacoId, dataEvento, null);
        }).doesNotThrowAnyException();
        
        verify(reservaRepositoryPort, times(2)).existsActiveReservationByEspacoAndData(espacoId, dataEvento, null);
    }

    @Test
    @DisplayName("Deve validar disponibilidade com data futura distante")
    void deveValidarDisponibilidadeComDataFuturaDistante() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(365); // 1 ano no futuro
        
        when(reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, dataEvento, null))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> reservaAvailabilityService.validarDisponibilidade(espacoId, dataEvento, null))
                .doesNotThrowAnyException();
        
        verify(reservaRepositoryPort).existsActiveReservationByEspacoAndData(espacoId, dataEvento, null);
    }
}

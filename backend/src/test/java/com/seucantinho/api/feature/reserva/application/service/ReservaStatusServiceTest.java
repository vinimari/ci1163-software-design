package com.seucantinho.api.feature.reserva.application.service;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.strategy.StatusTransitionStrategy;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaStatusService")
class ReservaStatusServiceTest {

    @Mock
    private StatusTransitionStrategy strategy1;

    @Mock
    private StatusTransitionStrategy strategy2;

    @InjectMocks
    private ReservaStatusService reservaStatusService;

    private List<StatusTransitionStrategy> transitionStrategies;
    private Reserva reserva;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        transitionStrategies = Arrays.asList(strategy1, strategy2);
        reservaStatusService = new ReservaStatusService(transitionStrategies);

        reserva = Reserva.builder()
                .id(1)
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .valorTotal(ValorMonetario.of("300.00"))
                .pagamentos(new ArrayList<>())
                .build();

        pagamento = Pagamento.builder()
                .id(1)
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
    }

    @Test
    @DisplayName("Deve atualizar status após pagamento com sucesso")
    void deveAtualizarStatusAposPagamentoComSucesso() {
        // Arrange
        when(strategy1.canHandle(pagamento)).thenReturn(true);
        when(strategy1.determineNewStatus(pagamento)).thenReturn(StatusReservaEnum.CONFIRMADA);

        // Act
        reservaStatusService.updateStatusAfterPayment(reserva, pagamento);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CONFIRMADA);
        verify(strategy1).canHandle(pagamento);
        verify(strategy1).determineNewStatus(pagamento);
    }

    @Test
    @DisplayName("Deve usar segunda estratégia quando primeira não pode tratar")
    void deveUsarSegundaEstrategiaQuandoPrimeiraNaoPodeTratar() {
        // Arrange
        reserva.setStatus(StatusReservaEnum.CONFIRMADA);
        when(strategy1.canHandle(pagamento)).thenReturn(false);
        when(strategy2.canHandle(pagamento)).thenReturn(true);
        when(strategy2.determineNewStatus(pagamento)).thenReturn(StatusReservaEnum.QUITADA);

        // Act
        reservaStatusService.updateStatusAfterPayment(reserva, pagamento);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.QUITADA);
        verify(strategy1).canHandle(pagamento);
        verify(strategy2).canHandle(pagamento);
        verify(strategy2).determineNewStatus(pagamento);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhuma estratégia pode tratar")
    void deveLancarExcecaoQuandoNenhumaEstrategiaPodeTratar() {
        // Arrange
        when(strategy1.canHandle(pagamento)).thenReturn(false);
        when(strategy2.canHandle(pagamento)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> reservaStatusService.updateStatusAfterPayment(reserva, pagamento))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Nenhuma estratégia de transição encontrada para o tipo de pagamento");

        verify(strategy1).canHandle(pagamento);
        verify(strategy2).canHandle(pagamento);
        verify(strategy1, never()).determineNewStatus(any());
        verify(strategy2, never()).determineNewStatus(any());
    }

    @Test
    @DisplayName("Deve cancelar reserva com sucesso")
    void deveCancelarReservaComSucesso() {
        // Arrange
        reserva.getPagamentos().add(pagamento);
        assertThat(reserva.getPagamentos()).hasSize(1);

        // Act
        reservaStatusService.cancelReservation(reserva);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CANCELADA);
        assertThat(reserva.getPagamentos()).isEmpty();
    }

    @Test
    @DisplayName("Deve cancelar reserva sem pagamentos")
    void deveCancelarReservaSemPagamentos() {
        // Arrange
        assertThat(reserva.getPagamentos()).isEmpty();

        // Act
        reservaStatusService.cancelReservation(reserva);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CANCELADA);
        assertThat(reserva.getPagamentos()).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar status com pagamento de sinal")
    void deveAtualizarStatusComPagamentoDeSinal() {
        // Arrange
        pagamento.setTipo(TipoPagamentoEnum.SINAL);
        when(strategy1.canHandle(pagamento)).thenReturn(true);
        when(strategy1.determineNewStatus(pagamento)).thenReturn(StatusReservaEnum.CONFIRMADA);

        // Act
        reservaStatusService.updateStatusAfterPayment(reserva, pagamento);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CONFIRMADA);
    }

    @Test
    @DisplayName("Deve atualizar status com pagamento total")
    void deveAtualizarStatusComPagamentoTotal() {
        // Arrange
        reserva.setStatus(StatusReservaEnum.CONFIRMADA);
        pagamento.setTipo(TipoPagamentoEnum.TOTAL);
        when(strategy1.canHandle(pagamento)).thenReturn(true);
        when(strategy1.determineNewStatus(pagamento)).thenReturn(StatusReservaEnum.QUITADA);

        // Act
        reservaStatusService.updateStatusAfterPayment(reserva, pagamento);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.QUITADA);
    }

    @Test
    @DisplayName("Deve atualizar status com pagamento de quitação")
    void deveAtualizarStatusComPagamentoDeQuitacao() {
        // Arrange
        reserva.setStatus(StatusReservaEnum.CONFIRMADA);
        pagamento.setTipo(TipoPagamentoEnum.QUITACAO);
        when(strategy1.canHandle(pagamento)).thenReturn(true);
        when(strategy1.determineNewStatus(pagamento)).thenReturn(StatusReservaEnum.QUITADA);

        // Act
        reservaStatusService.updateStatusAfterPayment(reserva, pagamento);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.QUITADA);
    }
}

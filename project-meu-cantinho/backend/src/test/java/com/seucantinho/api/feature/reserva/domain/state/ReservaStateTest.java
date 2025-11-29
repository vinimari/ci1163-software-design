package com.seucantinho.api.feature.reserva.domain.state;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes das transições de estado da reserva usando State Pattern")
class ReservaStateTest {

    @Test
    @DisplayName("Deve permitir transição de AGUARDANDO_SINAL para CONFIRMADA")
    void devePermitirTransicaoDeAguardandoSinalParaConfirmada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.AGUARDANDO_SINAL);

        // Act
        reserva.transitionToStatus(StatusReservaEnum.CONFIRMADA);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CONFIRMADA);
    }

    @Test
    @DisplayName("Deve permitir transição de AGUARDANDO_SINAL para CANCELADA")
    void devePermitirTransicaoDeAguardandoSinalParaCancelada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.AGUARDANDO_SINAL);

        // Act
        reserva.transitionToStatus(StatusReservaEnum.CANCELADA);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CANCELADA);
    }

    @Test
    @DisplayName("Deve permitir transição de AGUARDANDO_SINAL para QUITADA")
    void devePermitirTransicaoDeAguardandoSinalParaQuitada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.AGUARDANDO_SINAL);

        // Act
        reserva.transitionToStatus(StatusReservaEnum.QUITADA);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.QUITADA);
    }

    @Test
    @DisplayName("Deve permitir transição de CONFIRMADA para QUITADA")
    void devePermitirTransicaoDeConfirmadaParaQuitada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.CONFIRMADA);

        // Act
        reserva.transitionToStatus(StatusReservaEnum.QUITADA);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.QUITADA);
    }

    @Test
    @DisplayName("Deve permitir transição de CONFIRMADA para FINALIZADA")
    void devePermitirTransicaoDeConfirmadaParaFinalizada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.CONFIRMADA);

        // Act
        reserva.transitionToStatus(StatusReservaEnum.FINALIZADA);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.FINALIZADA);
    }

    @Test
    @DisplayName("Deve bloquear transição de CONFIRMADA para AGUARDANDO_SINAL")
    void deveBloquearTransicaoDeConfirmadaParaAguardandoSinal() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.CONFIRMADA);

        // Act & Assert
        assertThatThrownBy(() -> reserva.transitionToStatus(StatusReservaEnum.AGUARDANDO_SINAL))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Transição inválida");
    }

    @Test
    @DisplayName("Deve permitir transição de QUITADA para FINALIZADA")
    void devePermitirTransicaoDeQuitadaParaFinalizada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.QUITADA);

        // Act
        reserva.transitionToStatus(StatusReservaEnum.FINALIZADA);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.FINALIZADA);
    }

    @Test
    @DisplayName("Deve bloquear transição de QUITADA para CONFIRMADA")
    void deveBloquearTransicaoDeQuitadaParaConfirmada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.QUITADA);

        // Act & Assert
        assertThatThrownBy(() -> reserva.transitionToStatus(StatusReservaEnum.CONFIRMADA))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Transição inválida");
    }

    @Test
    @DisplayName("Deve bloquear qualquer transição de CANCELADA (estado terminal)")
    void deveBloquearTransicaoDeCancelada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.CANCELADA);

        // Act & Assert
        assertThatThrownBy(() -> reserva.transitionToStatus(StatusReservaEnum.CONFIRMADA))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("estado terminal");
    }

    @Test
    @DisplayName("Deve bloquear qualquer transição de FINALIZADA (estado terminal)")
    void deveBloquearTransicaoDeFinalizada() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> reserva.transitionToStatus(StatusReservaEnum.QUITADA))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("estado terminal");
    }

    @Test
    @DisplayName("Deve permitir transição idempotente (mesmo status)")
    void devePermitirTransicaoIdempotente() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.CONFIRMADA);

        // Act
        reserva.transitionToStatus(StatusReservaEnum.CONFIRMADA);

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CONFIRMADA);
    }

    @Test
    @DisplayName("Deve verificar se transição é possível antes de executar")
    void deveVerificarSePodeTransicionar() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.AGUARDANDO_SINAL);

        // Act & Assert
        assertThat(reserva.canTransitionTo(StatusReservaEnum.CONFIRMADA)).isTrue();
        assertThat(reserva.canTransitionTo(StatusReservaEnum.CANCELADA)).isTrue();
        assertThat(reserva.canTransitionTo(StatusReservaEnum.QUITADA)).isTrue();
        assertThat(reserva.canTransitionTo(StatusReservaEnum.FINALIZADA)).isFalse();
    }

    @Test
    @DisplayName("Deve identificar estados terminais corretamente")
    void deveIdentificarEstadosTerminais() {
        // Arrange & Act & Assert
        ReservaState canceladaState = ReservaStateFactory.createState(StatusReservaEnum.CANCELADA);
        ReservaState finalizadaState = ReservaStateFactory.createState(StatusReservaEnum.FINALIZADA);
        ReservaState confirmadaState = ReservaStateFactory.createState(StatusReservaEnum.CONFIRMADA);

        assertThat(canceladaState.isTerminal()).isTrue();
        assertThat(finalizadaState.isTerminal()).isTrue();
        assertThat(confirmadaState.isTerminal()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar descrição do estado")
    void deveRetornarDescricaoDoEstado() {
        // Arrange
        Reserva reserva = criarReserva(StatusReservaEnum.AGUARDANDO_SINAL);

        // Act
        String description = reserva.getState().getDescription();

        // Assert
        assertThat(description).isNotEmpty();
        assertThat(description).contains("sinal");
    }

    private Reserva criarReserva(StatusReservaEnum status) {
        Reserva reserva = Reserva.builder()
            .id(1)
            .status(status)
            .build();
        return reserva;
    }
}

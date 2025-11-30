package com.seucantinho.api.feature.reserva.domain.state;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;

public interface ReservaState {

    StatusReservaEnum getStatus();

    void transitionTo(Reserva reserva, StatusReservaEnum targetStatus);

    boolean canTransitionTo(StatusReservaEnum targetStatus);

    boolean isTerminal();

    String getDescription();
}

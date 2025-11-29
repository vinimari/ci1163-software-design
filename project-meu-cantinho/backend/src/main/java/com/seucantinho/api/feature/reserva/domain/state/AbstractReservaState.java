package com.seucantinho.api.feature.reserva.domain.state;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.shared.domain.exception.BusinessException;

import java.util.Set;

public abstract class AbstractReservaState implements ReservaState {

    protected abstract Set<StatusReservaEnum> getAllowedTransitions();

    @Override
    public void transitionTo(Reserva reserva, StatusReservaEnum targetStatus) {
        if (getStatus() == targetStatus) {
            return;
        }

        if (isTerminal()) {
            throw new BusinessException(
                String.format("Reserva com status %s não pode ser alterada (estado terminal)",
                    getStatus().name())
            );
        }

        if (!canTransitionTo(targetStatus)) {
            throw new BusinessException(
                String.format("Transição inválida: não é possível mudar de %s para %s",
                    getStatus().name(),
                    targetStatus.name())
            );
        }

        ReservaState newState = ReservaStateFactory.createState(targetStatus);
        reserva.setState(newState);
    }

    @Override
    public boolean canTransitionTo(StatusReservaEnum targetStatus) {
        return getAllowedTransitions().contains(targetStatus);
    }

    @Override
    public boolean isTerminal() {
        return false;
    }
}

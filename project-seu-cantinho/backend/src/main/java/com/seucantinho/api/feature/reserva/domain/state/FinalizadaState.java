package com.seucantinho.api.feature.reserva.domain.state;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;

import java.util.EnumSet;
import java.util.Set;

public class FinalizadaState extends AbstractReservaState {

    @Override
    public StatusReservaEnum getStatus() {
        return StatusReservaEnum.FINALIZADA;
    }

    @Override
    protected Set<StatusReservaEnum> getAllowedTransitions() {
        return EnumSet.noneOf(StatusReservaEnum.class);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Reserva finalizada - evento realizado (estado final)";
    }
}

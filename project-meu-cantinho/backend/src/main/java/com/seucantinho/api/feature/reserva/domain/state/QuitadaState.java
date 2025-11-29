package com.seucantinho.api.feature.reserva.domain.state;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;

import java.util.EnumSet;
import java.util.Set;

public class QuitadaState extends AbstractReservaState {

    @Override
    public StatusReservaEnum getStatus() {
        return StatusReservaEnum.QUITADA;
    }

    @Override
    protected Set<StatusReservaEnum> getAllowedTransitions() {
        return EnumSet.of(StatusReservaEnum.FINALIZADA);
    }

    @Override
    public String getDescription() {
        return "Reserva totalmente paga, aguardando realização do evento";
    }
}

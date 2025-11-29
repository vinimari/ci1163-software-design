package com.seucantinho.api.feature.reserva.domain.state;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;

import java.util.EnumSet;
import java.util.Set;

public class ConfirmadaState extends AbstractReservaState {

    @Override
    public StatusReservaEnum getStatus() {
        return StatusReservaEnum.CONFIRMADA;
    }

    @Override
    protected Set<StatusReservaEnum> getAllowedTransitions() {
        return EnumSet.of(
            StatusReservaEnum.QUITADA,
            StatusReservaEnum.CANCELADA,
            StatusReservaEnum.FINALIZADA
        );
    }

    @Override
    public String getDescription() {
        return "Reserva confirmada, aguardando quitação total";
    }
}

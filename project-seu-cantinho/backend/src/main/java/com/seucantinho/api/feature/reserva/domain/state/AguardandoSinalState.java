package com.seucantinho.api.feature.reserva.domain.state;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;

import java.util.EnumSet;
import java.util.Set;

public class AguardandoSinalState extends AbstractReservaState {

    @Override
    public StatusReservaEnum getStatus() {
        return StatusReservaEnum.AGUARDANDO_SINAL;
    }

    @Override
    protected Set<StatusReservaEnum> getAllowedTransitions() {
        return EnumSet.of(
            StatusReservaEnum.CONFIRMADA,
            StatusReservaEnum.QUITADA,
            StatusReservaEnum.CANCELADA
        );
    }

    @Override
    public String getDescription() {
        return "Aguardando pagamento do sinal para confirmar a reserva";
    }
}

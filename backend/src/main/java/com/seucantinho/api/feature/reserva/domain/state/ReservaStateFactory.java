package com.seucantinho.api.feature.reserva.domain.state;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;

import java.util.EnumMap;
import java.util.Map;

public class ReservaStateFactory {

    private static final Map<StatusReservaEnum, ReservaState> STATE_INSTANCES = new EnumMap<>(StatusReservaEnum.class);

    static {
        STATE_INSTANCES.put(StatusReservaEnum.AGUARDANDO_SINAL, new AguardandoSinalState());
        STATE_INSTANCES.put(StatusReservaEnum.CONFIRMADA, new ConfirmadaState());
        STATE_INSTANCES.put(StatusReservaEnum.QUITADA, new QuitadaState());
        STATE_INSTANCES.put(StatusReservaEnum.CANCELADA, new CanceladaState());
        STATE_INSTANCES.put(StatusReservaEnum.FINALIZADA, new FinalizadaState());
    }

    public static ReservaState createState(StatusReservaEnum status) {
        ReservaState state = STATE_INSTANCES.get(status);
        if (state == null) {
            throw new IllegalArgumentException("Status de reserva inválido: " + status);
        }
        return state;
    }

    private ReservaStateFactory() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }
}

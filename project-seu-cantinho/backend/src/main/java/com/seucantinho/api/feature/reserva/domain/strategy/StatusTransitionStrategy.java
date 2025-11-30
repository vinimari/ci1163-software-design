package com.seucantinho.api.feature.reserva.domain.strategy;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;

public interface StatusTransitionStrategy {

    StatusReservaEnum determineNewStatus(Pagamento pagamento);

    boolean canHandle(Pagamento pagamento);
}


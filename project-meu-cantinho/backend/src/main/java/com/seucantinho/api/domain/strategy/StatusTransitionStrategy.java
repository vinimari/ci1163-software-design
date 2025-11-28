package com.seucantinho.api.domain.strategy;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.enums.StatusReservaEnum;

public interface StatusTransitionStrategy {

    StatusReservaEnum determineNewStatus(Pagamento pagamento);

    boolean canHandle(Pagamento pagamento);
}


package com.seucantinho.api.feature.reserva.domain.strategy.impl;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.feature.reserva.domain.strategy.StatusTransitionStrategy;
import org.springframework.stereotype.Component;

@Component
public class TotalStatusTransitionStrategy implements StatusTransitionStrategy {

    @Override
    public StatusReservaEnum determineNewStatus(Pagamento pagamento) {
        return StatusReservaEnum.QUITADA;
    }

    @Override
    public boolean canHandle(Pagamento pagamento) {
        return pagamento.getTipo() == TipoPagamentoEnum.TOTAL;
    }
}


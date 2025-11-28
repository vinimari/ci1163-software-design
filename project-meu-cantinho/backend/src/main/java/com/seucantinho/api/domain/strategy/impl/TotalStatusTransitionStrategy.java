package com.seucantinho.api.domain.strategy.impl;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.domain.strategy.StatusTransitionStrategy;
import org.springframework.stereotype.Component;

/**
 * Estratégia para transição de status quando recebe pagamento TOTAL.
 * Transição: AGUARDANDO_SINAL -> QUITADA
 */
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


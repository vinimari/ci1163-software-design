package com.seucantinho.api.domain.strategy.impl;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.domain.strategy.StatusTransitionStrategy;
import org.springframework.stereotype.Component;

/**
 * Estratégia para transição de status quando recebe pagamento de SINAL.
 * Transição: AGUARDANDO_SINAL -> CONFIRMADA
 */
@Component
public class SinalStatusTransitionStrategy implements StatusTransitionStrategy {

    @Override
    public StatusReservaEnum determineNewStatus(Pagamento pagamento) {
        return StatusReservaEnum.CONFIRMADA;
    }

    @Override
    public boolean canHandle(Pagamento pagamento) {
        return pagamento.getTipo() == TipoPagamentoEnum.SINAL;
    }
}


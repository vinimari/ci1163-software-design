package com.seucantinho.api.domain.strategy;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.enums.StatusReservaEnum;

/**
 * Interface para implementar o padrão Strategy para transições de status de reserva.
 * Segue o princípio Open/Closed (OCP) - aberto para extensão, fechado para modificação.
 */
public interface StatusTransitionStrategy {

    /**
     * Determina o novo status da reserva baseado no tipo de pagamento recebido.
     *
     * @param pagamento O pagamento realizado
     * @return O novo status da reserva
     */
    StatusReservaEnum determineNewStatus(Pagamento pagamento);

    /**
     * Verifica se esta estratégia pode ser aplicada para o tipo de pagamento.
     *
     * @param pagamento O pagamento a ser processado
     * @return true se esta estratégia pode processar este tipo de pagamento
     */
    boolean canHandle(Pagamento pagamento);
}


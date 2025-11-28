package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.domain.strategy.StatusTransitionStrategy;
import com.seucantinho.api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço de domínio responsável por gerenciar as transições de status de reservas.
 * Aplica o padrão Strategy para determinar transições de estado de forma extensível.
 * 
 * Princípios aplicados:
 * - Single Responsibility Principle (SRP): Única responsabilidade de gerenciar transições
 * - Open/Closed Principle (OCP): Aberto para extensão (novas estratégias), fechado para modificação
 * - Dependency Inversion Principle (DIP): Depende de abstrações (StatusTransitionStrategy)
 */
@Service
@RequiredArgsConstructor
public class ReservaStatusService {

    private final List<StatusTransitionStrategy> transitionStrategies;

    /**
     * Atualiza o status da reserva baseado em um pagamento recebido.
     * 
     * @param reserva A reserva a ter o status atualizado
     * @param pagamento O pagamento que desencadeia a transição
     */
    public void updateStatusAfterPayment(Reserva reserva, Pagamento pagamento) {
        StatusTransitionStrategy strategy = findStrategyForPayment(pagamento);
        StatusReservaEnum newStatus = strategy.determineNewStatus(pagamento);
        reserva.setStatus(newStatus);
    }

    /**
     * Cancela uma reserva, removendo todos os pagamentos associados.
     * 
     * @param reserva A reserva a ser cancelada
     */
    public void cancelReservation(Reserva reserva) {
        reserva.setStatus(StatusReservaEnum.CANCELADA);
        reserva.getPagamentos().clear();
    }

    /**
     * Encontra a estratégia apropriada para processar o tipo de pagamento.
     * 
     * @param pagamento O pagamento a ser processado
     * @return A estratégia apropriada
     * @throws BusinessException se nenhuma estratégia for encontrada
     */
    private StatusTransitionStrategy findStrategyForPayment(Pagamento pagamento) {
        return transitionStrategies.stream()
            .filter(strategy -> strategy.canHandle(pagamento))
            .findFirst()
            .orElseThrow(() -> new BusinessException(
                "Nenhuma estratégia de transição encontrada para o tipo de pagamento: " + 
                pagamento.getTipo()
            ));
    }
}


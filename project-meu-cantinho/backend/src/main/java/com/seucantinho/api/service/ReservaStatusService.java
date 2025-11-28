package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.domain.strategy.StatusTransitionStrategy;
import com.seucantinho.api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaStatusService {

    private final List<StatusTransitionStrategy> transitionStrategies;

    public void updateStatusAfterPayment(Reserva reserva, Pagamento pagamento) {
        StatusTransitionStrategy strategy = findStrategyForPayment(pagamento);
        StatusReservaEnum newStatus = strategy.determineNewStatus(pagamento);
        reserva.setStatus(newStatus);
    }

    public void cancelReservation(Reserva reserva) {
        reserva.setStatus(StatusReservaEnum.CANCELADA);
        reserva.getPagamentos().clear();
    }

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


package com.seucantinho.api.validator;

import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Validador responsável pelas regras de negócio relacionadas a pagamentos.
 * Aplica o princípio SRP (Single Responsibility Principle).
 */
@Component
public class PagamentoValidator {

    public void validateValorPagamento(BigDecimal valorPagamento, Reserva reserva) {
        BigDecimal totalPago = reserva.calcularTotalPago();
        BigDecimal novoTotal = totalPago.add(valorPagamento);

        if (novoTotal.compareTo(reserva.getValorTotal()) > 0) {
            throw new BusinessException("Valor do pagamento excede o saldo da reserva");
        }
    }
}
